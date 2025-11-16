package com.nguyendat.linkedin.controller;

import com.nguyendat.linkedin.dto.request.LoginRequest;
import com.nguyendat.linkedin.dto.request.RegisterRequest;
import com.nguyendat.linkedin.entity.User;
import com.nguyendat.linkedin.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (req.email == null || req.password == null) {
            return ResponseEntity.badRequest().body("email and password required");
        }
        if (userRepository.findByEmail(req.email).isPresent()) {
            return ResponseEntity.status(409).body("email already registered");
        }
        User u = new User();
        u.setEmail(req.email);
        u.setPasswordHash(passwordEncoder.encode(req.password));
        u.setFullName(req.fullName);
        userRepository.save(u);
        return ResponseEntity.status(201).body(u);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        return userRepository.findByEmail(req.email)
                .map(u -> {
                    if (passwordEncoder.matches(req.password, u.getPasswordHash())) {
                        // For now return user as a placeholder for auth token
                        return ResponseEntity.ok(u);
                    }
                    return ResponseEntity.status(401).body("invalid credentials");
                }).orElse(ResponseEntity.status(401).body("invalid credentials"));
    }

    // TODO: logout, refresh, forgot-password, reset-password, verify-email
}
