package com.reliaquest.api;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.reliaquest.api.dto.EmployeeRequestDTO;
import com.reliaquest.api.validator.EmployeeValidator;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;

@SpringBootTest
class EmployeeValidatorTest {

    private EmployeeValidator validator;

    @BeforeEach
    void setUp() {
        MessageSource messageSource = mock(MessageSource.class);
        validator = new EmployeeValidator(messageSource);
    }

    @Test
    void testValidateUUID_valid() {
        String validUUID = UUID.randomUUID().toString();
        assertDoesNotThrow(() -> validator.validateUUID(validUUID));
    }

    @Test
    void testValidateEmployeeName_valid() {
        assertDoesNotThrow(() -> validator.validateEmployeeName("John"));
    }

    @Test
    void testValidateSalary_valid() {
        assertDoesNotThrow(() -> validator.validateSalary(3000));
    }

    @Test
    void testValidateTitle_valid() {
        assertDoesNotThrow(() -> validator.validateTitle("Engineer"));
    }

    // ========== validateEmployeeRequest ==========
    @Test
    void testValidateEmployeeRequest_valid() {
        EmployeeRequestDTO dto =
                new EmployeeRequestDTO(UUID.randomUUID(), "John", 50000, 25, "Engineer", "john@example.com");
        assertDoesNotThrow(() -> validator.validateEmployeeRequest(dto));
    }
}
