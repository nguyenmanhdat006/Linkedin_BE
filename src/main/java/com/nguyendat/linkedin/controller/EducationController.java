package com.nguyendat.linkedin.controller;

import com.nguyendat.linkedin.dto.request.EducationRequest;
import com.nguyendat.linkedin.dto.response.ApiResponse;
import com.nguyendat.linkedin.dto.response.EducationResponse;
import com.nguyendat.linkedin.service.EducationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/educations")
@RequiredArgsConstructor
public class EducationController {
    private final EducationService educationService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<EducationResponse>>> listByUser(@PathVariable Long userId) {
        var data = educationService.listByUser(userId);
        return ResponseEntity.ok(ApiResponse.success(data, "Educations for user"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EducationResponse>> get(@PathVariable Long id) {
        var data = educationService.getEducation(id);
        return ResponseEntity.ok(ApiResponse.success(data, "Education retrieved"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EducationResponse>> create(@RequestBody EducationRequest req) {
        var data = educationService.createEducation(req);
        return ResponseEntity.ok(ApiResponse.success(data, "Education created"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EducationResponse>> update(@PathVariable Long id, @RequestBody EducationRequest req) {
        var data = educationService.updateEducation(id, req);
        return ResponseEntity.ok(ApiResponse.success(data, "Education updated"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
        educationService.deleteEducation(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Education deleted"));
    }
}
