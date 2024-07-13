package com.example.review_service.dto;

import lombok.Data;

@Data
public class ReviewDto {
    private Long id;
    private Long jobId;
    private Long companyId;
    private Double rating;
    private String description;
}
