package com.reliaquest.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

/**
 * Record representing an Employee object returned by the external API.
 */
public record Employee(
        @JsonProperty("id") UUID id,
        @JsonProperty("employee_name") String name,
        @JsonProperty("employee_salary") int salary,
        @JsonProperty("employee_age") int age,
        @JsonProperty("employee_title") String title,
        @JsonProperty("employee_email") String email) {}
