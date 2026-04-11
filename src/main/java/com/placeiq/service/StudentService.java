package com.placeiq.service;

import com.placeiq.dto.AuthResponse;
import com.placeiq.model.User;
import com.placeiq.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final UserRepository userRepository;

    public List<AuthResponse.UserDto> getAllStudents() {
        return userRepository.findByRole("student")
                .stream()
                .map(AuthService::toDto)
                .collect(Collectors.toList());
    }

    public AuthResponse.UserDto getStudentById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        return AuthService.toDto(user);
    }
}
