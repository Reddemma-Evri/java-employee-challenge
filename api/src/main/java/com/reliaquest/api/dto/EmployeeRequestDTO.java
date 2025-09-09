package com.reliaquest.api.dto;

import java.util.UUID;

public record EmployeeRequestDTO(UUID id, String name, int salary, int age, String title, String email) {
}
