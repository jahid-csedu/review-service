package com.example.review_service.review;

import com.example.review_service.dto.ReviewDetailDto;
import com.example.review_service.dto.ReviewDto;
import com.example.review_service.external.company.CompanyClient;
import com.example.review_service.external.company.CompanyDto;
import com.example.review_service.external.job.JobClient;
import com.example.review_service.external.job.JobDto;
import com.example.review_service.mapper.ReviewMapper;
import com.example.review_service.mq.ReviewEventProducer;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final CompanyClient companyClient;
    private final JobClient jobClient;
    private final ReviewEventProducer reviewEventProducer;

    public Long create(ReviewDto reviewDto) {
        log.debug("Creating Review: {}", reviewDto);
        Review review = reviewRepository.save(reviewMapper.dtoToEntity(reviewDto));
        reviewEventProducer.sendMessage(review);
        return review.getId();
    }

    public ReviewDto update(Long id, ReviewDto reviewDto) {
        log.debug("Creating Review: {}", reviewDto);
        Review oldReview = reviewRepository.findById(id).orElse(null);
        if (oldReview != null) {
            reviewMapper.toUpdatedDto(oldReview, reviewDto);
            Review newReview = reviewRepository.save(oldReview);
            return reviewMapper.entityToDto(newReview);
        }

        return null;
    }

    public void delete(Long id) {
        log.debug("Deleting review by ID: {}", id);
        reviewRepository.findById(id).ifPresent(review -> reviewRepository.deleteById(id));
    }

    public ReviewDetailDto findById(Long id) {
        log.debug("Getting reviewOptional by ID: {}", id);
        Optional<Review> reviewOptional = reviewRepository.findById(id);
        if (reviewOptional.isEmpty()) {
            return null;
        }
        Review review = reviewOptional.get();
        ReviewDetailDto reviewDetailDto = reviewMapper.entityToDetailDto(review);
        reviewDetailDto.setCompany(companyClient.getCompanyById(review.getCompanyId()));
        reviewDetailDto.setJob(jobClient.getJobById(review.getJobId()));

        return reviewDetailDto;
    }

//    @CircuitBreaker(
//            name = "companyBreaker",
//            fallbackMethod = "companyBreakerFallback"
//    )
//    @Retry(
//            name = "companyBreaker",
//            fallbackMethod = "companyBreakerFallback"
//    )
    @RateLimiter(
            name = "companyBreaker"
    )
    public List<ReviewDetailDto> findByCompanyId(Long companyId) {
        List<Review> reviews = reviewRepository.findByCompanyId(companyId);
        List<ReviewDetailDto> reviewDetails = reviewMapper.entityToDetailDtoList(reviews);
        addCompanyDetails(reviewDetails, companyId);
        addJobDetails(reviewDetails);

        return reviewDetails;
    }

    public List<String> companyBreakerFallback(Exception e) {
        return Collections.emptyList();
    }



    public Double findCompanyRating(Long companyId) {
        log.info("Getting rating for company with ID: {}", companyId);
        List<Review> companyReviews = reviewRepository.findByCompanyId(companyId);
        return companyReviews.stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0);
    }
    private void addCompanyDetails(List<ReviewDetailDto> reviewDetailDtos, Long companyId) {
        CompanyDto company = companyClient.getCompanyById(companyId);
        reviewDetailDtos
                .forEach(review -> review.setCompany(company));
    }

    private void addJobDetails(List<ReviewDetailDto> reviewDetailDtos) {
        List<JobDto> jobResponse = jobClient.getAllJobs();
        Map<Long, JobDto> jobs = Objects.requireNonNull(jobResponse).stream()
                .collect(Collectors.toMap(JobDto::getId, Function.identity()));

        reviewDetailDtos
                .forEach(review -> review.setJob(jobs.get(review.getId())));
    }
}
