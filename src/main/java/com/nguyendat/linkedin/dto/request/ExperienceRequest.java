package com.nguyendat.linkedin.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ExperienceRequest {
    private Long userId;
    private String title;
    private String company;
    private String location;
    private String employmentType; // enum name
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isCurrent;
    private String description;
    private Integer displayOrder;
}
