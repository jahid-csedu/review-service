package com.example.review_service.review;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    ReviewDto entityToDto(Review review);
    ReviewDetailDto entityToDetailDto(Review review);
    List<ReviewDetailDto> entityToDetailDtoList(List<Review> review);
    Review dtoToEntity(ReviewDto reviewDto);
    List<ReviewDto> entityToDtoList(List<Review> reviews);
    @Mapping(target = "id", ignore = true)
    void toUpdatedDto(@MappingTarget Review review, ReviewDto reviewDto);
}
