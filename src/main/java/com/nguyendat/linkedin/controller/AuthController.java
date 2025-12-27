package com.nguyendat.linkedin.controller;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nguyendat.linkedin.dto.request.LoginRequest;
import com.nguyendat.linkedin.dto.request.RegisterRequest;
import com.nguyendat.linkedin.dto.request.VerifyRequest;
import com.nguyendat.linkedin.dto.response.ApiResponse;
import com.nguyendat.linkedin.entity.User;
import com.nguyendat.linkedin.service.UserService;
import com.nguyendat.linkedin.util.JwtUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final UserService userService;
    private final JwtUtils jwtUtils;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegisterRequest request) {
        userService.registerUser(request.getEmail(), request.getPassword(), request.getFirstname(), request.getLastname());
        return ResponseEntity.ok(ApiResponse.success("Vui lòng kiểm tra email để xác nhận tài khoản", "Registration successful"));
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<String>> verify(@Valid @RequestBody VerifyRequest request) {
        boolean success = userService.verifyUser(request.getEmail(), request.getCode());
        String message = success ? "Xác nhận thành công" : "Mã xác nhận không hợp lệ";
        return ResponseEntity.ok(ApiResponse.success(message, message));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = userService.getUserByEmail(request.getEmail());
        if (!user.isEnabled()) {
            return ResponseEntity.ok(ApiResponse.success("Tài khoản chưa xác nhận email", "Account not verified"));
        }

        Set<String> roles = user.getRoles().stream().map(r -> r.getName()).collect(Collectors.toSet());
        String token = jwtUtils.generateToken(user.getEmail(), roles, user.getSlug(), user.getId());
        return ResponseEntity.ok(ApiResponse.success(token, "Login successful"));
    }
}
