package com.nguyendat.linkedin.controller;

import com.nguyendat.linkedin.dto.request.*;
import com.nguyendat.linkedin.entity.User;
import com.nguyendat.linkedin.service.UserService;
import com.nguyendat.linkedin.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final UserService userService;
    private final JwtUtils jwtUtils;

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        userService.registerUser(request.getEmail(), request.getPassword(), request.getFirstname(), request.getLastname());
        return "Vui lòng kiểm tra email để xác nhận tài khoản";
    }

    @PostMapping("/verify")
    public String verify(@RequestBody VerifyRequest request) {
        boolean success = userService.verifyUser(request.getEmail(), request.getCode());
        return success ? "Xác nhận thành công" : "Mã xác nhận không hợp lệ";
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = userService.getUserByEmail(request.getEmail());
        if (!user.isEnabled()) return "Tài khoản chưa xác nhận email";

        Set<String> roles = user.getRoles().stream().map(r -> r.getName()).collect(Collectors.toSet());
        return jwtUtils.generateToken(user.getEmail(), roles, user.getSlug());
    }
}
