package com.reliaquest.api.controller;

import com.reliaquest.api.dto.EmployeeRequestDTO;
import com.reliaquest.api.exception.TooManyRequestsException;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.service.EmployeeServiceImpl;
import com.reliaquest.api.service.IEmployeeService;
import com.reliaquest.api.validator.EmployeeValidator;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
public class EmployeeControllerImpl implements IEmployeeController<Employee, EmployeeRequestDTO> {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    private final IEmployeeService employeeService;

    private final EmployeeValidator employeeValidator;

    @Autowired
    public EmployeeControllerImpl(IEmployeeService employeeService, EmployeeValidator employeeValidator) {
        this.employeeService = employeeService;
        this.employeeValidator = employeeValidator;
    }

    @Override
    @Retryable(
            value = {TooManyRequestsException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 3000, multiplier = 2))
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    @Recover
    public List<Employee> fallbackGetAllEmployees(TooManyRequestsException ex) {
        // fallback logic when all retries fail
        logger.error("Fallback triggered for getAllEmployees: {}", ex.getMessage());
        return List.of();
    }

    @Override
    public ResponseEntity<Employee> getEmployeeById(@PathVariable String id) {
        employeeValidator.validateUUID(id);
        Employee employee = employeeService.getEmployeeById(UUID.fromString(id));
        return ResponseEntity.ok(employee);
    }

    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(@PathVariable String searchString) {
        employeeValidator.validateEmployeeName(searchString);
        List<Employee> employees = employeeService.getEmployeesByNameSearch(searchString);
        return ResponseEntity.ok(employees);
    }

    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        int highestSalary = employeeService.getHighestSalary();
        return ResponseEntity.ok(highestSalary);
    }

    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        List<String> names = employeeService.getTopTenHighestEarningEmployeeNames();
        return ResponseEntity.ok(names);
    }

    @Override
    public ResponseEntity<Employee> createEmployee(@RequestBody EmployeeRequestDTO employeeInput) {
        employeeValidator.validateEmployeeRequest(employeeInput);
        Employee employee = employeeService.createEmployee(employeeInput);
        return ResponseEntity.ok(employee);
    }

    @Override
    public ResponseEntity<String> deleteEmployeeById(@PathVariable String id) {
        employeeValidator.validateUUID(id);
        employeeService.deleteEmployeeById(UUID.fromString(id));
        return ResponseEntity.ok("Deleted employee with id: " + id);
    }
}
