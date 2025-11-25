package com.nguyendat.linkedin.service;

import com.nguyendat.linkedin.dto.response.UserProfileResponse;
import com.nguyendat.linkedin.entity.User;
import com.nguyendat.linkedin.entity.UserSkill;
import com.nguyendat.linkedin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserProfileService {
    
    private final UserRepository userRepository;
    
    /**
     * Get user profile by ID with all details
     * @param userId - ID of user to view
     * @param currentUserId - ID of current logged-in user
     * @return UserProfileResponse
     */
    public UserProfileResponse getUserProfile(Long userId, Long currentUserId) {
        // Get user with all relationships
        User user = userRepository.findByIdWithDetails(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        // Get statistics
        Integer connectionCount = userRepository.countConnections(userId);
        Integer followerCount = userRepository.countFollowers(userId);
        Integer followingCount = userRepository.countFollowing(userId);
        
        // Check relationship with current user
        boolean isOwnProfile = userId.equals(currentUserId);
        Boolean isConnected = false;
        String connectionStatus = null;
        Boolean isFollowing = false;
        
        if (!isOwnProfile && currentUserId != null) {
            isConnected = userRepository.areConnected(currentUserId, userId);
            connectionStatus = userRepository.getConnectionStatus(currentUserId, userId)
                .orElse(null);
            isFollowing = userRepository.isFollowing(currentUserId, userId);
        }
        
        // Map experiences
        var experiences = user.getExperiences().stream()
            .sorted(Comparator.comparing(exp -> exp.getStartDate(), Comparator.reverseOrder()))
            .map(exp -> UserProfileResponse.ExperienceDto.builder()
                .id(exp.getId())
                .title(exp.getTitle())
                .company(exp.getCompany())
                .location(exp.getLocation())
                .employmentType(exp.getEmploymentType() != null ? exp.getEmploymentType().name() : null)
                .startDate(exp.getStartDate())
                .endDate(exp.getEndDate())
                .isCurrent(exp.getIsCurrent())
                .description(exp.getDescription())
                .build())
            .collect(Collectors.toList());
        
        // Map educations
        var educations = user.getEducations().stream()
            .sorted(Comparator.comparing(edu -> edu.getStartDate(), 
                    Comparator.nullsLast(Comparator.reverseOrder())))
            .map(edu -> UserProfileResponse.EducationDto.builder()
                .id(edu.getId())
                .school(edu.getSchool())
                .degree(edu.getDegree())
                .fieldOfStudy(edu.getFieldOfStudy())
                .startDate(edu.getStartDate())
                .endDate(edu.getEndDate())
                .grade(edu.getGrade())
                .activities(edu.getActivities())
                .description(edu.getDescription())
                .build())
            .collect(Collectors.toList());
        
        // Map skills
        var skills = user.getUserSkills().stream()
            .sorted(Comparator.comparing(UserSkill::getEndorsementCount, Comparator.reverseOrder()))
            .map(us -> UserProfileResponse.SkillDto.builder()
                .id(us.getSkill().getId())
                .name(us.getSkill().getName())
                .category(us.getSkill().getCategory())
                .endorsementCount(us.getEndorsementCount())
                .isEndorsedByCurrentUser(false) // TODO: Implement endorsement check
                .build())
            .collect(Collectors.toList());
        
        // Build response
        return UserProfileResponse.builder()
            .id(user.getId())
            .email(isOwnProfile ? user.getEmail() : null) // Hide email if not own profile
            .fullName(user.getFullName())
            .headline(user.getHeadline())
            .avatarUrl(user.getAvatarUrl())
            .bannerUrl(user.getBannerUrl())
            .location(user.getLocation())
            .about(user.getAbout())
            .website(user.getWebsite())
            .phone(isOwnProfile ? user.getPhone() : null) // Hide phone if not own profile
            .isVerified(user.isVerified())
            .isActive(user.isActive())
            .connectionCount(connectionCount)
            .followerCount(followerCount)
            .followingCount(followingCount)
            .isOwnProfile(isOwnProfile)
            .isConnected(isConnected)
            .connectionStatus(connectionStatus)
            .isFollowing(isFollowing)
            .createdAt(user.getCreatedAt())
            .experiences(experiences)
            .educations(educations)
            .skills(skills)
            .build();
    }
    
    /**
     * Get current user profile
     */
    public UserProfileResponse getCurrentUserProfile(Long userId) {
        return getUserProfile(userId, userId);
    }
}