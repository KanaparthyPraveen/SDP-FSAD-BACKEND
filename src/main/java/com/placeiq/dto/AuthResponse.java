package com.placeiq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private UserDto user;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserDto {
        private String id;
        private String name;
        private String email;
        private String role;
        private String department;
        private String branch;
        private String rollNo;
        private Integer year;
        private Double cgpa;
        private Integer backlogs;
        private String phone;
        private String bio;
        private java.util.List<String> skills;
        private String linkedIn;
        private String github;
        private String avatar;
        private String resume;
        private String resumeName;
        private java.time.LocalDate createdAt;
    }
}
