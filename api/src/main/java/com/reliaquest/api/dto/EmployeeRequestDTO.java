package com.reliaquest.api.dto;

import jakarta.validation.constraints.*;

public record EmployeeRequestDTO(
        @NotBlank(message = "{employee.name.notBlank}") String name,
        @NotNull(message = "{employee.salary.notBlank}") @Positive(message = "{employee.salary.positive}") Integer salary,
        @NotNull(message = "{employee.age.notBlank}") @Min(value = 16, message = "{employee.age.range}")
                @Max(value = 75, message = "{employee.age.range}")
                Integer age,
        @NotBlank(message = "{employee.title.notBlank}") String title) {}
