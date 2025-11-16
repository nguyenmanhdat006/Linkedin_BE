package com.nguyendat.linkedin.repository;

import com.nguyendat.linkedin.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
}
