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
@Document(collection = "companies")
public class Company {

    @Id
    private String id;

    private String name;
    private String logo;
    private String shortName;
    private String role;
    private String ctc;
    private String packageOffered;
    private String location;
    private String type;
    private Double minCgpa;
    private Integer maxBacklogs;
    private List<String> allowedBranches;
    private String eligibility;
    private String deadline;
    private String description;
    private String aboutCompany;
    private List<String> skills;
    private Integer openings;

    /** "active" | "closed" | "upcoming" */
    private String status;

    private String dresscode;
    private List<CompanyRound> rounds;
    private Integer registrations;
    private CtcBreakdown ctcBreakdown;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompanyRound {
        private String name;
        private String type;
        private String duration;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CtcBreakdown {
        private String base;
        private String bonus;
        private String stocks;
    }
}
