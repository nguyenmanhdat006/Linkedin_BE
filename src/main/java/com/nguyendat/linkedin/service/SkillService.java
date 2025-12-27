package com.nguyendat.linkedin.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nguyendat.linkedin.dto.request.SkillRequest;
import com.nguyendat.linkedin.dto.response.SkillResponse;
import com.nguyendat.linkedin.entity.Skill;
import com.nguyendat.linkedin.entity.User;
import com.nguyendat.linkedin.exception.ResourceNotFoundException;
import com.nguyendat.linkedin.repository.SkillRepository;
import com.nguyendat.linkedin.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SkillService {
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;

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
            .orElseThrow(() -> new ResourceNotFoundException("Skill not found: " + id));
        if (req.getName() != null) skill.setName(req.getName());
        skill.setCategory(req.getCategory());
        skill.setDescription(req.getDescription());
        skill = skillRepository.save(skill);
        return toResponse(skill);
    }

    public void deleteSkill(Long id) {
        Skill skill = skillRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Skill not found: " + id));
        // detach from users first to avoid FK constraint
        skill.getUsers().forEach(u -> u.getSkills().remove(skill));
        // save affected users
        skill.getUsers().forEach(userRepository::save);
        skillRepository.delete(skill);
    }

    @Transactional(readOnly = true)
    public SkillResponse getSkill(Long id) {
        return skillRepository.findById(id).map(this::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Skill not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<SkillResponse> listSkillsByUser(Long userId) {
        User user = userRepository.findByIdWithDetails(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        return user.getSkills().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public SkillResponse addSkillToUser(Long userId, SkillRequest req) {
        User user = userRepository.findByIdWithDetails(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        Skill skill = skillRepository.findByName(req.getName()).orElseGet(() -> {
            Skill s = Skill.builder()
                .name(req.getName())
                .category(req.getCategory())
                .description(req.getDescription())
                .build();
            return skillRepository.save(s);
        });

        // add skill to user if not already present
        if (!user.getSkills().contains(skill)) {
            user.getSkills().add(skill);
            userRepository.save(user);
        }

        return toResponse(skill);
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
