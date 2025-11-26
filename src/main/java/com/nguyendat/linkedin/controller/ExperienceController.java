package com.nguyendat.linkedin.controller;

import com.nguyendat.linkedin.dto.request.ExperienceRequest;
import com.nguyendat.linkedin.dto.response.ApiResponse;
import com.nguyendat.linkedin.dto.response.ExperienceResponse;
import com.nguyendat.linkedin.service.ExperienceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/experiences")
@RequiredArgsConstructor
public class ExperienceController {
    private final ExperienceService experienceService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<ExperienceResponse>>> listByUser(@PathVariable Long userId) {
        var data = experienceService.listByUser(userId);
        return ResponseEntity.ok(ApiResponse.success(data, "Experiences for user"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExperienceResponse>> get(@PathVariable Long id) {
        var data = experienceService.getExperience(id);
        return ResponseEntity.ok(ApiResponse.success(data, "Experience retrieved"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ExperienceResponse>> create(@RequestBody ExperienceRequest req) {
        var data = experienceService.createExperience(req);
        return ResponseEntity.ok(ApiResponse.success(data, "Experience created"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ExperienceResponse>> update(@PathVariable Long id, @RequestBody ExperienceRequest req) {
        var data = experienceService.updateExperience(id, req);
        return ResponseEntity.ok(ApiResponse.success(data, "Experience updated"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
        experienceService.deleteExperience(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Experience deleted"));
    }
}
