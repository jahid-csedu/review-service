package com.example.review_service.external.company;

import lombok.Data;

@Data
public class CompanyDto {
    private Long id;
    private String name;
    private String location;
    private Integer numberOfEmployees;
}
