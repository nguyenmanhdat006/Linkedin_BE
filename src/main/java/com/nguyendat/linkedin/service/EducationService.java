package com.nguyendat.linkedin.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nguyendat.linkedin.dto.request.EducationRequest;
import com.nguyendat.linkedin.dto.response.EducationResponse;
import com.nguyendat.linkedin.entity.Education;
import com.nguyendat.linkedin.entity.User;
import com.nguyendat.linkedin.exception.ResourceNotFoundException;
import com.nguyendat.linkedin.repository.EducationRepository;
import com.nguyendat.linkedin.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class EducationService {
    private final EducationRepository educationRepository;
    private final UserRepository userRepository;

    public EducationResponse createEducation(EducationRequest req) {
        User user = userRepository.findById(req.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + req.getUserId()));
        Education edu = Education.builder()
            .user(user)
            .school(req.getSchool())
            .degree(req.getDegree())
            .fieldOfStudy(req.getFieldOfStudy())
            .startDate(req.getStartDate())
            .endDate(req.getEndDate())
            .grade(req.getGrade())
            .activities(req.getActivities())
            .description(req.getDescription())
            .displayOrder(req.getDisplayOrder() == null ? 0 : req.getDisplayOrder())
            .build();
        edu = educationRepository.save(edu);
        return toResponse(edu);
    }

    public EducationResponse updateEducation(Long id, EducationRequest req) {
        Education edu = educationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Education not found: " + id));
        if (req.getSchool() != null) edu.setSchool(req.getSchool());
        if (req.getDegree() != null) edu.setDegree(req.getDegree());
        if (req.getFieldOfStudy() != null) edu.setFieldOfStudy(req.getFieldOfStudy());
        if (req.getStartDate() != null) edu.setStartDate(req.getStartDate());
        if (req.getEndDate() != null) edu.setEndDate(req.getEndDate());
        if (req.getGrade() != null) edu.setGrade(req.getGrade());
        if (req.getActivities() != null) edu.setActivities(req.getActivities());
        if (req.getDescription() != null) edu.setDescription(req.getDescription());
        if (req.getDisplayOrder() != null) edu.setDisplayOrder(req.getDisplayOrder());
        edu = educationRepository.save(edu);
        return toResponse(edu);
    }

    public void deleteEducation(Long id) {
        if (!educationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Education not found: " + id);
        }
        educationRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public EducationResponse getEducation(Long id) {
        return educationRepository.findById(id).map(this::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Education not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<EducationResponse> listByUser(Long userId) {
        return educationRepository.findByUserId(userId).stream().map(this::toResponse).collect(Collectors.toList());
    }

    private EducationResponse toResponse(Education e) {
        return EducationResponse.builder()
            .id(e.getId())
            .userId(e.getUser() != null ? e.getUser().getId() : null)
            .school(e.getSchool())
            .degree(e.getDegree())
            .fieldOfStudy(e.getFieldOfStudy())
            .startDate(e.getStartDate())
            .endDate(e.getEndDate())
            .grade(e.getGrade())
            .activities(e.getActivities())
            .description(e.getDescription())
            .displayOrder(e.getDisplayOrder())
            .createdAt(e.getCreatedAt())
            .updatedAt(e.getUpdatedAt())
            .build();
    }
}
