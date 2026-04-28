package com.placeiq.service;

import com.placeiq.dto.PredictionRequest;
import com.placeiq.dto.PredictionResponse;
import com.placeiq.model.Company;
import com.placeiq.model.User;
import com.placeiq.repository.CompanyRepository;
import com.placeiq.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    @Lazy private final NotificationService notificationService;
    @Lazy private final PredictionService predictionService;

    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    public List<Company> getActiveCompanies() {
        return companyRepository.findByStatus("active");
    }

    public Company getCompanyById(String id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Company not found with id: " + id));
    }

    public Company createCompany(Company company) {
        if (company.getStatus() == null) company.setStatus("active");
        if (company.getRegistrations() == null) company.setRegistrations(0);
        Company saved = companyRepository.save(company);
        // Async: notify matching students
        notifyMatchingStudents(saved);
        return saved;
    }

    @Async
    public void notifyMatchingStudents(Company company) {
        try {
            List<User> students = userRepository.findByRole("student");
            for (User student : students) {
                try {
                    PredictionResponse prediction = predictionService.predictRuleBased(student, company);
                    if (prediction.getProbability() >= 0.60) {
                        int pct = (int) Math.round(prediction.getProbability() * 100);
                        notificationService.createAndPush(
                            student.getId(),
                            "NEW_COMPANY",
                            "🏢 New Company — " + company.getName() + " (" + pct + "% Match!)",
                            company.getName() + " is hiring for " + company.getRole()
                                + ". You are a " + pct + "% match! Apply before " + company.getDeadline(),
                            "/companies/" + company.getId()
                        );
                    }
                } catch (Exception e) {
                    log.warn("Skipping notification for student {}: {}", student.getId(), e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Failed to run notifyMatchingStudents: {}", e.getMessage());
        }
    }

    public Company updateCompany(String id, Company updates) {
        Company existing = getCompanyById(id);
        if (updates.getName() != null)           existing.setName(updates.getName());
        if (updates.getLogo() != null)           existing.setLogo(updates.getLogo());
        if (updates.getRole() != null)           existing.setRole(updates.getRole());
        if (updates.getCtc() != null)            existing.setCtc(updates.getCtc());
        if (updates.getLocation() != null)       existing.setLocation(updates.getLocation());
        if (updates.getOpenings() != null)       existing.setOpenings(updates.getOpenings());
        if (updates.getMinCgpa() != null)        existing.setMinCgpa(updates.getMinCgpa());
        if (updates.getMaxBacklogs() != null)    existing.setMaxBacklogs(updates.getMaxBacklogs());
        if (updates.getDeadline() != null)       existing.setDeadline(updates.getDeadline());
        if (updates.getDescription() != null)    existing.setDescription(updates.getDescription());
        if (updates.getSkills() != null)         existing.setSkills(updates.getSkills());
        if (updates.getAllowedBranches() != null) existing.setAllowedBranches(updates.getAllowedBranches());
        if (updates.getStatus() != null)         existing.setStatus(updates.getStatus());
        if (updates.getRounds() != null)         existing.setRounds(updates.getRounds());
        if (updates.getCtcBreakdown() != null)   existing.setCtcBreakdown(updates.getCtcBreakdown());
        return companyRepository.save(existing);
    }

    public void deleteCompany(String id) {
        if (!companyRepository.existsById(id)) {
            throw new IllegalArgumentException("Company not found with id: " + id);
        }
        companyRepository.deleteById(id);
    }

    public List<Company> searchCompanies(String query) {
        return companyRepository.findByNameContainingIgnoreCase(query);
    }
}
