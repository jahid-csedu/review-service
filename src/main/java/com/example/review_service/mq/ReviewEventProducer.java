package com.example.review_service.mq;

import com.example.review_service.review.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewEventProducer {
    private final RabbitTemplate rabbitTemplate;

    public void sendMessage(Review review) {
        ReviewEvent reviewEvent = ReviewEvent.builder()
                .id(review.getId())
                .description(review.getDescription())
                .rating(review.getRating())
                .companyId(review.getCompanyId())
                .build();

        rabbitTemplate.convertAndSend("companyRatingQueue", reviewEvent);
    }
}
