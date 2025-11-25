package com.nguyendat.linkedin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private Long id;
    private String email;
    private String fullName;
    private String headline;
    private String avatarUrl;
    private String bannerUrl;
    private String location;
    private String about;
    private String website;
    private String phone;
    private boolean isVerified;
    private boolean isActive;
    
    // Statistics
    private Integer connectionCount;
    private Integer followerCount;
    private Integer followingCount;
    
    // Relationship with current user
    private Boolean isOwnProfile;
    private Boolean isConnected;
    private String connectionStatus; // null, PENDING, ACCEPTED
    private Boolean isFollowing;
    
    private LocalDateTime createdAt;
    
    // Related data
    private List<ExperienceDto> experiences;
    private List<EducationDto> educations;
    private List<SkillDto> skills;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExperienceDto {
        private Long id;
        private String title;
        private String company;
        private String location;
        private String employmentType;
        private LocalDate startDate;
        private LocalDate endDate;
        private Boolean isCurrent;
        private String description;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EducationDto {
        private Long id;
        private String school;
        private String degree;
        private String fieldOfStudy;
        private LocalDate startDate;
        private LocalDate endDate;
        private Double grade;
        private String activities;
        private String description;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkillDto {
        private Long id;
        private String name;
        private String category;
        private Integer endorsementCount;
        private Boolean isEndorsedByCurrentUser;
    }
}