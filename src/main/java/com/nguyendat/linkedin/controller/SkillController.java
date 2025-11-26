package com.nguyendat.linkedin.controller;

import com.nguyendat.linkedin.dto.request.SkillRequest;
import com.nguyendat.linkedin.dto.response.ApiResponse;
import com.nguyendat.linkedin.dto.response.SkillResponse;
import com.nguyendat.linkedin.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SkillResponse>>> list() {
        var data = skillService.listSkills();
        return ResponseEntity.ok(ApiResponse.success(data, "List of skills"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SkillResponse>> get(@PathVariable Long id) {
        var data = skillService.getSkill(id);
        return ResponseEntity.ok(ApiResponse.success(data, "Skill retrieved"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SkillResponse>> create(@RequestBody SkillRequest req) {
        var data = skillService.createSkill(req);
        return ResponseEntity.ok(ApiResponse.success(data, "Skill created"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SkillResponse>> update(@PathVariable Long id, @RequestBody SkillRequest req) {
        var data = skillService.updateSkill(id, req);
        return ResponseEntity.ok(ApiResponse.success(data, "Skill updated"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
        skillService.deleteSkill(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Skill deleted"));
    }
}
