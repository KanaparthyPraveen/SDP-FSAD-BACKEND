package com.placeiq.dto;

import lombok.Data;
import java.util.List;

@Data
public class UpdateProfileRequest {
    private String name;
    private String phone;
    private String bio;
    private String department;
    private String branch;
    private Integer year;
    private Double cgpa;
    private Integer backlogs;
    private List<String> skills;
    private String linkedIn;
    private String github;
    private String avatar;
    private String resume;
    private String resumeName;
}
