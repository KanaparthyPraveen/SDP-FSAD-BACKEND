package com.placeiq.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "applications")
public class Application {

    @Id
    private String id;

    private String companyId;
    private String companyName;
    private String companyLogo;
    private String role;
    private String packageOffered;

    /**
     * Tracks the student who applied.
     * Stores a snapshot of student data at time of application.
     */
    private ApplicantSnapshot applicant;

    /** "applied" | "shortlisted" | "interview" | "selected" | "rejected" */
    private String status;

    private String currentRound;
    private String appliedDate;
    private String appliedAt;
    private String coverLetter;
    private Double probability;
    private List<ApplicationRound> rounds;
    private String notes;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApplicationRound {
        private String name;
        /** "PENDING" | "ONGOING" | "CLEARED" | "REJECTED" */
        private String status;
        private String completedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApplicantSnapshot {
        private String userId;
        private String name;
        private String email;
        private String phone;
        private String department;
        private String rollNo;
        private Integer year;
        private Double cgpa;
        private Integer backlogs;
        private List<String> skills;
        private String linkedIn;
        private String github;
        private String resume;
        private String resumeName;
    }
}
