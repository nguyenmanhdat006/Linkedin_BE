package com.nguyendat.linkedin.repository;

import com.nguyendat.linkedin.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Long> {
}
