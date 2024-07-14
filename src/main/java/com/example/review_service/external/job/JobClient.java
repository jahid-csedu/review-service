package com.example.review_service.external.job;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "JOB-SERVICE")
public interface JobClient {

    @GetMapping("/jobs/{id}")
    JobDto getJobById(@PathVariable Long id);

    @GetMapping("/jobs")
    List<JobDto> getAllJobs();
}
