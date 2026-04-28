package com.placeiq.service;

import com.placeiq.dto.AdminUpdateApplicationRequest;
import com.placeiq.dto.ApplyRequest;
import com.placeiq.model.Application;
import com.placeiq.model.Company;
import com.placeiq.model.User;
import com.placeiq.repository.ApplicationRepository;
import com.placeiq.repository.CompanyRepository;
import com.placeiq.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    @Lazy
    private final NotificationService notificationService;

    /* ─── Student: Apply ─── */
    public Application apply(String userId, ApplyRequest req) {
        if (applicationRepository.existsByApplicantUserIdAndCompanyId(userId, req.getCompanyId())) {
            throw new IllegalArgumentException("You have already applied to this company.");
        }

        Company company = companyRepository.findById(req.getCompanyId())
                .orElseThrow(() -> new IllegalArgumentException("Company not found. It may have been removed."));

        // No slots remaining
        if (company.getOpenings() != null && company.getOpenings() <= 0) {
            throw new IllegalArgumentException("No slots available for this company.");
        }

        // Deadline check
        if (company.getDeadline() != null && !company.getDeadline().isBlank()) {
            try {
                LocalDate deadline = LocalDate.parse(company.getDeadline());
                if (deadline.isBefore(LocalDate.now())) {
                    throw new IllegalArgumentException("Application deadline has passed.");
                }
            } catch (Exception e) {
                if (e instanceof IllegalArgumentException) throw e;
            }
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User account not found."));

        // Strict CGPA eligibility check
        if (company.getMinCgpa() != null && company.getMinCgpa() > 0) {
            double studentCgpa = user.getCgpa() != null ? user.getCgpa() : 0.0;
            if (studentCgpa < company.getMinCgpa()) {
                throw new IllegalArgumentException(
                    String.format("CGPA %.1f below required %.1f for %s.", studentCgpa, company.getMinCgpa(), company.getName())
                );
            }
        }

        // Snapshot applicant data
        Application.ApplicantSnapshot snapshot = new Application.ApplicantSnapshot(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getDepartment(),
                user.getRollNo(),
                user.getYear(),
                user.getCgpa(),
                user.getBacklogs(),
                user.getSkills(),
                user.getLinkedIn(),
                user.getGithub(),
                user.getResume(),
                user.getResumeName()
        );

        // Map company rounds → application rounds
        List<Application.ApplicationRound> rounds = company.getRounds() != null
                ? company.getRounds().stream()
                    .map(r -> new Application.ApplicationRound(r.getName(), "PENDING", null))
                    .collect(Collectors.toList())
                : List.of();

        String now = LocalDate.now().toString();
        String nowIso = java.time.Instant.now().atZone(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);

        Application app = new Application();
        app.setCompanyId(company.getId());
        app.setCompanyName(company.getName());
        app.setCompanyLogo(company.getLogo());
        app.setRole(company.getRole());
        app.setPackageOffered(company.getCtc());
        app.setApplicant(snapshot);
        app.setStatus("applied");
        app.setCurrentRound(rounds.isEmpty() ? "Under Review" : rounds.get(0).getName());
        app.setAppliedDate(now);
        app.setAppliedAt(nowIso);
        app.setCoverLetter(req.getCoverLetter());
        app.setProbability(0.35 + Math.random() * 0.25);
        app.setRounds(rounds);

        if (company.getOpenings() != null && company.getOpenings() > 0) {
            company.setOpenings(company.getOpenings() - 1);
            if (company.getRegistrations() != null) {
                company.setRegistrations(company.getRegistrations() + 1);
            } else {
                company.setRegistrations(1);
            }
            companyRepository.save(company);
        }

        return applicationRepository.save(app);
    }

    /* ─── Student: My Applications ─── */
    public List<Application> getMyApplications(String userId) {
        return applicationRepository.findByApplicantUserIdOrderByAppliedAtDesc(userId);
    }

    /* ─── Admin: All Applications ─── */
    public List<Application> getAllApplications() {
        return applicationRepository.findAll();
    }

    /* ─── Admin: Update round or status ─── */
    public Application adminUpdate(String appId, AdminUpdateApplicationRequest req) {
        Application app = applicationRepository.findById(appId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        // Update a specific round
        if (req.getRoundIndex() != null && req.getRoundStatus() != null) {
            List<Application.ApplicationRound> rounds = app.getRounds();
            if (rounds != null && req.getRoundIndex() < rounds.size()) {
                rounds.get(req.getRoundIndex()).setStatus(req.getRoundStatus());
                if (!req.getRoundStatus().equals("PENDING")) {
                    rounds.get(req.getRoundIndex()).setCompletedAt(LocalDate.now().toString());
                } else {
                    rounds.get(req.getRoundIndex()).setCompletedAt(null);
                }
                // Auto-advance next round to ONGOING when this round is CLEARED
                if ("CLEARED".equals(req.getRoundStatus()) && req.getRoundIndex() + 1 < rounds.size()) {
                    rounds.get(req.getRoundIndex() + 1).setStatus("ONGOING");
                }
            }
            app.setRounds(rounds);
        }

        // Remember old status for notification comparison
        String oldStatus = app.getStatus();

        // Direct status override
        if (req.getStatus() != null) {
            app.setStatus(req.getStatus());
        } else {
            // Auto-derive status from rounds
            List<Application.ApplicationRound> rounds = app.getRounds();
            if (rounds != null && !rounds.isEmpty()) {
                boolean allCleared  = rounds.stream().allMatch(r -> "CLEARED".equals(r.getStatus()));
                boolean anyRejected = rounds.stream().anyMatch(r -> "REJECTED".equals(r.getStatus()));
                boolean hasOngoing  = rounds.stream().anyMatch(r -> "ONGOING".equals(r.getStatus()));
                long clearedCount   = rounds.stream().filter(r -> "CLEARED".equals(r.getStatus())).count();
                if (allCleared)           app.setStatus("selected");
                else if (anyRejected)     app.setStatus("rejected");
                else if (hasOngoing)      app.setStatus("interview");
                else if (clearedCount > 0) app.setStatus("shortlisted");
            }
        }

        // Update currentRound label (null-safe)
        List<Application.ApplicationRound> finalRounds = app.getRounds();
        if (finalRounds != null && !finalRounds.isEmpty()) {
            finalRounds.stream()
                .filter(r -> "ONGOING".equals(r.getStatus()))
                .findFirst()
                .ifPresentOrElse(
                    r -> app.setCurrentRound(r.getName()),
                    () -> finalRounds.stream()
                        .filter(r -> "PENDING".equals(r.getStatus()))
                        .findFirst()
                        .ifPresent(r -> app.setCurrentRound(r.getName()))
                );
        }

        if ("selected".equals(app.getStatus())) app.setCurrentRound("Completed");

        // Save notes
        if (req.getNotes() != null) app.setNotes(req.getNotes());

        Application saved = applicationRepository.save(app);

        // 🔔 Fire notification if status changed
        try {
            String studentId = saved.getApplicant() != null ? saved.getApplicant().getUserId() : null;
            if (studentId != null && !saved.getStatus().equals(oldStatus)) {
                String emoji = switch (saved.getStatus()) {
                    case "selected"    -> "🎉";
                    case "rejected"    -> "❌";
                    case "interview"   -> "📅";
                    case "shortlisted" -> "⭐";
                    default -> "📋";
                };
                notificationService.createAndPush(
                    studentId,
                    "STATUS_UPDATE",
                    emoji + " Application Update — " + saved.getCompanyName(),
                    "Your application for " + saved.getCompanyName() + " is now " + saved.getStatus().toUpperCase() + "!",
                    "/applications/" + saved.getId()
                );
            }
        } catch (Exception e) {
            log.warn("Failed to send notification: {}", e.getMessage());
        }

        return saved;
    }

    public List<Application> getApplicationsByCompany(String companyId) {
        return applicationRepository.findByCompanyId(companyId);
    }
}
