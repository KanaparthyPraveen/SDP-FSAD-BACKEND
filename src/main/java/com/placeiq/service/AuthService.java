package com.placeiq.service;

import com.placeiq.dto.*;
import com.placeiq.model.User;
import com.placeiq.repository.UserRepository;
import com.placeiq.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole(req.getRole() != null ? req.getRole() : "student");
        user.setDepartment(req.getDepartment() != null ? req.getDepartment() : "Computer Science");
        user.setBranch(req.getDepartment() != null ? req.getDepartment() : "Computer Science");
        user.setRollNo(req.getRollNo() != null ? req.getRollNo() : "");
        user.setYear(req.getYear() != null ? req.getYear() : 4);
        user.setCgpa(0.0);
        user.setBacklogs(0);
        user.setSkills(new ArrayList<>());
        user.setCreatedAt(LocalDate.now());

        User saved = userRepository.save(user);
        String token = jwtUtil.generateToken(saved.getEmail(), saved.getRole(), saved.getId());
        return new AuthResponse(token, toDto(saved));
    }

    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("No account found with this email"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole(), user.getId());
        return new AuthResponse(token, toDto(user));
    }

    public AuthResponse.UserDto updateProfile(String userId, UpdateProfileRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (req.getName() != null)       user.setName(req.getName());
        if (req.getPhone() != null)      user.setPhone(req.getPhone());
        if (req.getBio() != null)        user.setBio(req.getBio());
        if (req.getDepartment() != null) user.setDepartment(req.getDepartment());
        if (req.getBranch() != null)     user.setBranch(req.getBranch());
        if (req.getYear() != null)       user.setYear(req.getYear());
        if (req.getCgpa() != null)       user.setCgpa(req.getCgpa());
        if (req.getBacklogs() != null)   user.setBacklogs(req.getBacklogs());
        if (req.getSkills() != null)     user.setSkills(req.getSkills());
        if (req.getLinkedIn() != null)   user.setLinkedIn(req.getLinkedIn());
        if (req.getGithub() != null)     user.setGithub(req.getGithub());
        if (req.getAvatar() != null)     user.setAvatar(req.getAvatar());
        if (req.getResume() != null)     user.setResume(req.getResume());
        if (req.getResumeName() != null) user.setResumeName(req.getResumeName());

        User updated = userRepository.save(user);
        return toDto(updated);
    }

    // Map User → UserDto (omits password)
    public static AuthResponse.UserDto toDto(User u) {
        AuthResponse.UserDto dto = new AuthResponse.UserDto();
        dto.setId(u.getId());
        dto.setName(u.getName());
        dto.setEmail(u.getEmail());
        dto.setRole(u.getRole());
        dto.setDepartment(u.getDepartment());
        dto.setBranch(u.getBranch());
        dto.setRollNo(u.getRollNo());
        dto.setYear(u.getYear());
        dto.setCgpa(u.getCgpa());
        dto.setBacklogs(u.getBacklogs());
        dto.setPhone(u.getPhone());
        dto.setBio(u.getBio());
        dto.setSkills(u.getSkills());
        dto.setLinkedIn(u.getLinkedIn());
        dto.setGithub(u.getGithub());
        dto.setAvatar(u.getAvatar());
        dto.setResume(u.getResume());
        dto.setResumeName(u.getResumeName());
        dto.setCreatedAt(u.getCreatedAt());
        return dto;
    }
}
