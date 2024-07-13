package com.example.review_service.dto;

import com.example.review_service.external.company.CompanyDto;
import com.example.review_service.external.job.JobDto;
import lombok.Data;

@Data
public class ReviewDetailDto {
    private Long id;
    private Double rating;
    private String description;
    private JobDto job;
    private CompanyDto company;
}
