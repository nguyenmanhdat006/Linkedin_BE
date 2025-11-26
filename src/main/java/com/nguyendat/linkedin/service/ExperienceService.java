package com.nguyendat.linkedin.service;

import com.nguyendat.linkedin.dto.request.ExperienceRequest;
import com.nguyendat.linkedin.dto.response.ExperienceResponse;
import com.nguyendat.linkedin.entity.Experience;
import com.nguyendat.linkedin.entity.User;
import com.nguyendat.linkedin.entity.enums.EmploymentType;
import com.nguyendat.linkedin.repository.ExperienceRepository;
import com.nguyendat.linkedin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ExperienceService {
    private final ExperienceRepository experienceRepository;
    private final UserRepository userRepository;

    public ExperienceResponse createExperience(ExperienceRequest req) {
        User user = userRepository.findById(req.getUserId())
            .orElseThrow(() -> new RuntimeException("User not found: " + req.getUserId()));
        Experience exp = Experience.builder()
            .user(user)
            .title(req.getTitle())
            .company(req.getCompany())
            .location(req.getLocation())
            .employmentType(req.getEmploymentType() != null ? EmploymentType.valueOf(req.getEmploymentType()) : null)
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
            .orElseThrow(() -> new RuntimeException("Experience not found: " + id));
        if (req.getTitle() != null) exp.setTitle(req.getTitle());
        if (req.getCompany() != null) exp.setCompany(req.getCompany());
        if (req.getLocation() != null) exp.setLocation(req.getLocation());
        if (req.getEmploymentType() != null) exp.setEmploymentType(EmploymentType.valueOf(req.getEmploymentType()));
        if (req.getStartDate() != null) exp.setStartDate(req.getStartDate());
        if (req.getEndDate() != null) exp.setEndDate(req.getEndDate());
        if (req.getIsCurrent() != null) exp.setIsCurrent(req.getIsCurrent());
        if (req.getDescription() != null) exp.setDescription(req.getDescription());
        if (req.getDisplayOrder() != null) exp.setDisplayOrder(req.getDisplayOrder());
        exp = experienceRepository.save(exp);
        return toResponse(exp);
    }

    public void deleteExperience(Long id) {
        experienceRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public ExperienceResponse getExperience(Long id) {
        return experienceRepository.findById(id).map(this::toResponse)
            .orElseThrow(() -> new RuntimeException("Experience not found: " + id));
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
