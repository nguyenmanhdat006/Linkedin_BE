package com.nguyendat.linkedin.controller;

import com.nguyendat.linkedin.entity.Job;
import com.nguyendat.linkedin.entity.JobApplication;
import com.nguyendat.linkedin.entity.User;
import com.nguyendat.linkedin.repository.JobRepository;
import com.nguyendat.linkedin.repository.JobApplicationRepository;
import com.nguyendat.linkedin.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/jobs")
public class JobController {
    private final JobRepository jobRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final UserRepository userRepository;

    public JobController(JobRepository jobRepository, JobApplicationRepository jobApplicationRepository, UserRepository userRepository) {
        this.jobRepository = jobRepository;
        this.jobApplicationRepository = jobApplicationRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<?> list() {
        return ResponseEntity.ok(jobRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Job job) {
        jobRepository.save(job);
        return ResponseEntity.status(201).body(job);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return jobRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/apply")
    public ResponseEntity<?> apply(@PathVariable Long id, @RequestBody JobApplication app) {
        Optional<Job> jOpt = jobRepository.findById(id);
        Optional<User> uOpt = userRepository.findAll().stream().findFirst();
        if (jOpt.isEmpty() || uOpt.isEmpty()) return ResponseEntity.badRequest().build();
        app.setJob(jOpt.get());
        app.setUser(uOpt.get());
        jobApplicationRepository.save(app);
        return ResponseEntity.status(201).body(app);
    }

    @GetMapping("/applications")
    public ResponseEntity<?> applications() {
        return ResponseEntity.ok(jobApplicationRepository.findAll());
    }
}
