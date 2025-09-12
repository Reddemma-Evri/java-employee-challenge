package com.reliaquest.api.service;

import com.reliaquest.api.constant.ValidatorConstants;
import com.reliaquest.api.dto.EmployeeRequestDTO;
import com.reliaquest.api.model.Employee;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import org.springframework.validation.annotation.Validated;

@Validated
public interface IEmployeeService {

    List<Employee> getAllEmployees();

    List<Employee> getEmployeesByNameSearch(@Valid @NotBlank String searchString);

    Employee getEmployeeById(
            @Valid @Pattern(regexp = ValidatorConstants.UUID_VALIDATOR, message = "{employee.id.invalidUUID}")
                    String id);

    int getHighestSalary();

    List<String> getTopTenHighestEarningEmployeeNames();

    Employee createEmployee(EmployeeRequestDTO input);

    String deleteEmployeeById(
            @Valid @Pattern(regexp = ValidatorConstants.UUID_VALIDATOR, message = "{employee.id.invalidUUID}")
                    String id);
}
