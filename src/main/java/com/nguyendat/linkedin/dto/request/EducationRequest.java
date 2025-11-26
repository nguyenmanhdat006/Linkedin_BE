package com.nguyendat.linkedin.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class EducationRequest {
    private Long userId;
    private String school;
    private String degree;
    private String fieldOfStudy;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double grade;
    private String activities;
    private String description;
    private Integer displayOrder;
}
