package com.nguyendat.linkedin.controller;

import com.nguyendat.linkedin.dto.response.ApiResponse;
import com.nguyendat.linkedin.dto.response.UserProfileResponse;
import com.nguyendat.linkedin.security.UserPrincipal;
import com.nguyendat.linkedin.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class UserProfileController {
    
    private final UserProfileService userProfileService;
    
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getUserProfile(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserPrincipal currentUser 
    ) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiResponse.<UserProfileResponse>builder()
                    .success(false)
                    .message("Unauthorized: User not found in context")
                    .build()
            );
        }

        Long currentUserId = currentUser.getId(); 
        
        UserProfileResponse profile = userProfileService.getUserProfile(userId, currentUserId);
        
        return ResponseEntity.ok(ApiResponse.<UserProfileResponse>builder()
            .success(true)
            .message("Profile retrieved successfully")
            .data(profile)
            .build());
    }
}