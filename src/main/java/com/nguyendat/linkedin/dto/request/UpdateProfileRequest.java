package com.nguyendat.linkedin.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class UpdateProfileRequest {
        private String fullName;
        private String headline;
        private String country;
        private String city;
        private String industry;
        private String about;
        private String website;
        private String phone;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EducationRequest {
        private String school;
        private String degree;
        private String fieldOfStudy;
        private LocalDate startDate;
        private LocalDate endDate;
        private Double grade;
        private String activities;
        private String description;
    }
}
