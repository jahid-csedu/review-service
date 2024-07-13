package com.example.review_service.external.job;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobClient {
    private final RestTemplate restTemplate;
    private static final String JOB_URL = "http://JOB-SERVICE/jobs";

    public Map<Long, JobDto> getJobs() {
        ResponseEntity<JobDto[]> jobs = restTemplate.getForEntity(JOB_URL, JobDto[].class);

        return Arrays.stream(Objects.requireNonNull(jobs.getBody()))
                .collect(Collectors.toMap(JobDto::getId, Function.identity()));
    }

    public JobDto getJobById(Long jobId) {
        ResponseEntity<JobDto> job = restTemplate.getForEntity(String.format("%s/%s", JOB_URL, jobId), JobDto.class);

        return job.getBody();
    }
}
