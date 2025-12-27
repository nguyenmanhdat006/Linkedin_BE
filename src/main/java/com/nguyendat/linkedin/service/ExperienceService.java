package com.nguyendat.linkedin.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nguyendat.linkedin.dto.request.ExperienceRequest;
import com.nguyendat.linkedin.dto.response.ExperienceResponse;
import com.nguyendat.linkedin.entity.Experience;
import com.nguyendat.linkedin.entity.User;
import com.nguyendat.linkedin.entity.enums.EmploymentType;
import com.nguyendat.linkedin.exception.BadRequestException;
import com.nguyendat.linkedin.exception.ResourceNotFoundException;
import com.nguyendat.linkedin.repository.ExperienceRepository;
import com.nguyendat.linkedin.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ExperienceService {
    private final ExperienceRepository experienceRepository;
    private final UserRepository userRepository;

    public ExperienceResponse createExperience(ExperienceRequest req) {
        User user = userRepository.findById(req.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + req.getUserId()));
        
        // Validate and parse EmploymentType
        EmploymentType employmentType = null;
        if (req.getEmploymentType() != null && !req.getEmploymentType().trim().isEmpty()) {
            try {
                employmentType = EmploymentType.valueOf(req.getEmploymentType().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid employment type: " + req.getEmploymentType() + 
                    ". Valid values are: FULL_TIME, PART_TIME, CONTRACT, INTERNSHIP, FREELANCE, SELF_EMPLOYED");
            }
        }
        
        Experience exp = Experience.builder()
            .user(user)
            .title(req.getTitle())
            .company(req.getCompany())
            .location(req.getLocation())
            .employmentType(employmentType)
            .startDate(req.getStartDate())
            .endDate(req.getEndDate())
            .isCurrent(req.getIsCurrent() == null ? false : req.getIsCurrent())
            .description(req.getDescription())
            .displayOrder(req.getDisplayOrder() == null ? 0 : req.getDisplayOrder())
            .build();
        exp = experienceRepository.save(exp);
        return toResponse(exp);
    }

    public ExperienceResponse updateExperience(Long id, ExperienceRequest req) {
        Experience exp = experienceRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Experience not found: " + id));
        if (req.getTitle() != null) exp.setTitle(req.getTitle());
        if (req.getCompany() != null) exp.setCompany(req.getCompany());
        if (req.getLocation() != null) exp.setLocation(req.getLocation());
        if (req.getEmploymentType() != null && !req.getEmploymentType().trim().isEmpty()) {
            try {
                exp.setEmploymentType(EmploymentType.valueOf(req.getEmploymentType().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid employment type: " + req.getEmploymentType() + 
                    ". Valid values are: FULL_TIME, PART_TIME, CONTRACT, INTERNSHIP, FREELANCE, SELF_EMPLOYED");
            }
        }
        if (req.getStartDate() != null) exp.setStartDate(req.getStartDate());
        if (req.getEndDate() != null) exp.setEndDate(req.getEndDate());
        if (req.getIsCurrent() != null) exp.setIsCurrent(req.getIsCurrent());
        if (req.getDescription() != null) exp.setDescription(req.getDescription());
        if (req.getDisplayOrder() != null) exp.setDisplayOrder(req.getDisplayOrder());
        exp = experienceRepository.save(exp);
        return toResponse(exp);
    }

    public void deleteExperience(Long id) {
        if (!experienceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Experience not found: " + id);
        }
        experienceRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public ExperienceResponse getExperience(Long id) {
        return experienceRepository.findById(id).map(this::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Experience not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<ExperienceResponse> listByUser(Long userId) {
        return experienceRepository.findByUserId(userId).stream().map(this::toResponse).collect(Collectors.toList());
    }

    private ExperienceResponse toResponse(Experience e) {
        return ExperienceResponse.builder()
            .id(e.getId())
            .userId(e.getUser() != null ? e.getUser().getId() : null)
            .title(e.getTitle())
            .company(e.getCompany())
            .location(e.getLocation())
            .employmentType(e.getEmploymentType() != null ? e.getEmploymentType().name() : null)
            .startDate(e.getStartDate())
            .endDate(e.getEndDate())
            .isCurrent(e.getIsCurrent())
            .description(e.getDescription())
            .displayOrder(e.getDisplayOrder())
            .createdAt(e.getCreatedAt())
            .updatedAt(e.getUpdatedAt())
            .build();
    }
}
