package com.example.review_service.external.company;

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
public class CompanyClient {
    private final RestTemplate restTemplate;
    private static final String COMPANY_URL = "http://localhost:8082/companies";

    public Map<Long, CompanyDto> getCompanies() {
        ResponseEntity<CompanyDto[]> companies = restTemplate.getForEntity(COMPANY_URL, CompanyDto[].class);

        return Arrays.stream(Objects.requireNonNull(companies.getBody()))
                .collect(Collectors.toMap(CompanyDto::getId, Function.identity()));
    }

    public CompanyDto getCompanyById(Long companyId) {
        ResponseEntity<CompanyDto> company = restTemplate.getForEntity(String.format("%s/%s", COMPANY_URL, companyId), CompanyDto.class);

        return company.getBody();
    }
}
