package com.placeiq.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private String id;

    private String name;

    @Indexed(unique = true)
    private String email;

    private String password;

    /** "student" or "admin" */
    private String role;

    private String department;
    private String branch;
    private String rollNo;
    private Integer year;
    private Double cgpa;
    private Integer backlogs;
    private String phone;
    private String bio;
    private List<String> skills;
    private String linkedIn;
    private String github;
    private String avatar;
    private String resume;
    private String resumeName;
    private LocalDate createdAt;
}
