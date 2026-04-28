package com.placeiq.service;

import com.placeiq.dto.PredictionRequest;
import com.placeiq.dto.PredictionResponse;
import com.placeiq.model.Company;
import com.placeiq.model.User;
import com.placeiq.repository.CompanyRepository;
import com.placeiq.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PredictionService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private static final String GEMINI_URL =
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=";

    // ─── Main Entry Point ─────────────────────────────────────────────────────

    public PredictionResponse predict(PredictionRequest req) {
        User student = userRepository.findById(req.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Company company = companyRepository.findById(req.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        // Try Gemini first, fallback to rule-based
        try {
            return predictWithGemini(student, company);
        } catch (Exception e) {
            log.warn("Gemini prediction failed, falling back to rule-based: {}", e.getMessage());
            return predictRuleBased(student, company);
        }
    }

    // ─── Phase 3: Gemini AI Prediction ───────────────────────────────────────

    @SuppressWarnings("unchecked")
    private PredictionResponse predictWithGemini(User student, Company company) {
        RestTemplate restTemplate = new RestTemplate();

        String prompt = String.format("""
            You are a placement predictor AI. Analyze and return ONLY valid JSON (no markdown, no explanation).
            
            Student Profile:
            - CGPA: %s
            - Backlogs: %s
            - Skills: %s
            - Department: %s
            - Year: %s
            
            Company Requirements:
            - Name: %s
            - Min CGPA: %s
            - Max Backlogs: %s
            - Required Skills: %s
            - Type: %s
            - CTC: %s
            
            Return ONLY this JSON:
            {
              "probability": <number between 0 and 1>,
              "reasoning": "<2-3 sentence explanation>",
              "recommendation": "<one of: Highly Recommended, Good Match, Borderline, Not Eligible>"
            }
            """,
            student.getCgpa() != null ? student.getCgpa() : "Not specified",
            student.getBacklogs() != null ? student.getBacklogs() : 0,
            student.getSkills() != null ? String.join(", ", student.getSkills()) : "None",
            student.getDepartment() != null ? student.getDepartment() : "Not specified",
            student.getYear() != null ? student.getYear() : "Not specified",
            company.getName(),
            company.getMinCgpa() != null ? company.getMinCgpa() : "None",
            company.getMaxBacklogs() != null ? company.getMaxBacklogs() : "Not specified",
            company.getSkills() != null ? String.join(", ", company.getSkills()) : "None",
            company.getType() != null ? company.getType() : "Not specified",
            company.getCtc() != null ? company.getCtc() : "Not disclosed"
        );

        Map<String, Object> requestBody = Map.of(
            "contents", List.of(Map.of(
                "parts", List.of(Map.of("text", prompt))
            ))
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Map> response = restTemplate.postForEntity(
            GEMINI_URL + geminiApiKey,
            new HttpEntity<>(requestBody, headers),
            Map.class
        );

        // Parse Gemini response
        List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");
        Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
        String jsonText = (String) parts.get(0).get("text");

        // Strip any markdown code blocks if present
        jsonText = jsonText.replaceAll("```json", "").replaceAll("```", "").trim();

        // Parse JSON manually (avoid Jackson circular deps)
        double probability = extractDouble(jsonText, "probability");
        String reasoning = extractString(jsonText, "reasoning");
        String recommendation = extractString(jsonText, "recommendation");

        return new PredictionResponse(probability, reasoning, recommendation);
    }

    // ─── Phase 1: Rule-Based Fallback ─────────────────────────────────────────

    public PredictionResponse predictRuleBased(User student, Company company) {
        double cgpaScore = 0.5;
        double skillScore = 0.0;
        double backlogScore = 1.0;
        double deptScore = 0.7;

        List<String> reasons = new ArrayList<>();

        // CGPA Score (40%)
        if (student.getCgpa() != null && company.getMinCgpa() != null) {
            if (student.getCgpa() < company.getMinCgpa()) {
                cgpaScore = 0.0;
                reasons.add("CGPA " + student.getCgpa() + " is below minimum " + company.getMinCgpa());
            } else {
                cgpaScore = Math.min(1.0, (student.getCgpa() - company.getMinCgpa()) / (10.0 - company.getMinCgpa()));
                reasons.add("CGPA " + student.getCgpa() + " meets requirement of " + company.getMinCgpa());
            }
        }

        // Backlogs Score (15%)
        if (student.getBacklogs() != null && company.getMaxBacklogs() != null) {
            if (student.getBacklogs() > company.getMaxBacklogs()) {
                backlogScore = 0.0;
                reasons.add(student.getBacklogs() + " backlogs exceeds allowed " + company.getMaxBacklogs());
            } else {
                reasons.add("No backlog issues");
            }
        }

        // Skills Score (35%)
        if (student.getSkills() != null && company.getSkills() != null && !company.getSkills().isEmpty()) {
            long matched = student.getSkills().stream()
                    .filter(s -> company.getSkills().stream().anyMatch(cs -> cs.equalsIgnoreCase(s)))
                    .count();
            skillScore = (double) matched / company.getSkills().size();
            reasons.add(matched + "/" + company.getSkills().size() + " required skills matched");
        } else {
            skillScore = 0.5;
            reasons.add("Skill data incomplete");
        }

        // Department Score (10%)
        if (student.getDepartment() != null && company.getAllowedBranches() != null && !company.getAllowedBranches().isEmpty()) {
            boolean deptMatch = company.getAllowedBranches().stream()
                    .anyMatch(b -> b.equalsIgnoreCase(student.getDepartment()));
            deptScore = deptMatch ? 1.0 : 0.3;
            reasons.add(deptMatch ? "Department matches" : "Department mismatch");
        }

        double finalScore = (0.40 * cgpaScore) + (0.35 * skillScore) + (0.15 * backlogScore) + (0.10 * deptScore);
        finalScore = Math.max(0.0, Math.min(1.0, finalScore));

        String recommendation;
        if (finalScore >= 0.75) recommendation = "Highly Recommended";
        else if (finalScore >= 0.50) recommendation = "Good Match";
        else if (finalScore >= 0.30) recommendation = "Borderline";
        else recommendation = "Not Eligible";

        String reasoning = String.join(". ", reasons) + ".";

        return new PredictionResponse(Math.round(finalScore * 100.0) / 100.0, reasoning, recommendation);
    }

    // ─── JSON helpers ─────────────────────────────────────────────────────────

    private double extractDouble(String json, String key) {
        try {
            int start = json.indexOf("\"" + key + "\"") + key.length() + 3;
            int end = json.indexOf(",", start);
            if (end == -1) end = json.indexOf("}", start);
            return Double.parseDouble(json.substring(start, end).trim());
        } catch (Exception e) {
            return 0.5;
        }
    }

    private String extractString(String json, String key) {
        try {
            int start = json.indexOf("\"" + key + "\"");
            start = json.indexOf("\"", start + key.length() + 3) + 1;
            int end = json.indexOf("\"", start);
            return json.substring(start, end);
        } catch (Exception e) {
            return "Unable to parse";
        }
    }
}
