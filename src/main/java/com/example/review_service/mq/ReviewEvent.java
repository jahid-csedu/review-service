package com.example.review_service.mq;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewEvent {
    private Long id;
    private Long companyId;
    private Double rating;
    private String description;
}
