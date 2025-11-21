package com.nguyendat.linkedin.service;

import com.nguyendat.linkedin.entity.Role;
import com.nguyendat.linkedin.entity.User;
import com.nguyendat.linkedin.repository.RoleRepository;
import com.nguyendat.linkedin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public void registerUser(String email, String password) {
        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .enabled(false)
                .verificationCode(UUID.randomUUID().toString())
                .build();

        Role role = roleRepository.findByName("USER")
                .orElseGet(() -> roleRepository.save(Role.builder().name("USER").build()));

        user.getRoles().add(role);
        userRepository.save(user);

        // TODO: send verification email
        System.out.println("Verification code: " + user.getVerificationCode());
    }

    public boolean verifyUser(String email, String code) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) return false;

        User user = optionalUser.get();
        if (user.getVerificationCode().equals(code)) {
            user.setEnabled(true);
            user.setVerificationCode(null);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow();
    }
}
