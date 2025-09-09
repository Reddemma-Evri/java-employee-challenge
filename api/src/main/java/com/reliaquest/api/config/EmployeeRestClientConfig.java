package com.reliaquest.api.config;

import com.reliaquest.api.exception.TooManyRequestsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClient;

@Configuration
public class EmployeeRestClientConfig {

    @Value("${api.rate-limit.error}")
    private String rateLimitErrorMsg;

    @Bean(name = "employeeRestClient")
    public RestClient employeeRestClient(
            @Value("${employee.api.protocol}") String protocol,
            @Value("${employee.api.host}") String host,
            @Value("${employee.api.port}") String port,
            @Value("${employee.api.base-url}") String basePath) {
        String baseUrl = String.format("%s://%s:%s%s", protocol, host, port, basePath);
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultStatusHandler(
                        status -> status == HttpStatus.TOO_MANY_REQUESTS, // ✅ Predicate<HttpStatusCode>
                        (request, response) -> {
                            throw new TooManyRequestsException(rateLimitErrorMsg);
                        })
                .build();
    }
}
