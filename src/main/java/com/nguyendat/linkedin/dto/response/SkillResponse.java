package com.nguyendat.linkedin.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SkillResponse {
    private Long id;
    private String name;
    private String category;
    private String description;
    private LocalDateTime createdAt;
}
