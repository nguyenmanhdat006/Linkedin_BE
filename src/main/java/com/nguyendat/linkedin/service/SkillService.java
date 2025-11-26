package com.nguyendat.linkedin.service;

import com.nguyendat.linkedin.dto.request.SkillRequest;
import com.nguyendat.linkedin.dto.response.SkillResponse;
import com.nguyendat.linkedin.entity.Skill;
import com.nguyendat.linkedin.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SkillService {
    private final SkillRepository skillRepository;

    public SkillResponse createSkill(SkillRequest req) {
        Skill skill = Skill.builder()
            .name(req.getName())
            .category(req.getCategory())
            .description(req.getDescription())
            .build();
        skill = skillRepository.save(skill);
        return toResponse(skill);
    }

    public SkillResponse updateSkill(Long id, SkillRequest req) {
        Skill skill = skillRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Skill not found: " + id));
        if (req.getName() != null) skill.setName(req.getName());
        skill.setCategory(req.getCategory());
        skill.setDescription(req.getDescription());
        skill = skillRepository.save(skill);
        return toResponse(skill);
    }

    public void deleteSkill(Long id) {
        skillRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public SkillResponse getSkill(Long id) {
        return skillRepository.findById(id).map(this::toResponse)
            .orElseThrow(() -> new RuntimeException("Skill not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<SkillResponse> listSkills() {
        return skillRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    private SkillResponse toResponse(Skill s) {
        return SkillResponse.builder()
            .id(s.getId())
            .name(s.getName())
            .category(s.getCategory())
            .description(s.getDescription())
            .createdAt(s.getCreatedAt())
            .build();
    }
}
