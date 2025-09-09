package com.reliaquest.api.service;

import com.reliaquest.api.dto.EmployeeRequestDTO;
import com.reliaquest.api.model.Employee;
import java.util.List;
import java.util.UUID;

public interface IEmployeeService {

    List<Employee> getAllEmployees();

    List<Employee> getEmployeesByNameSearch(String searchString);

    Employee getEmployeeById(UUID id);

    int getHighestSalary();

    List<String> getTopTenHighestEarningEmployeeNames();

    Employee createEmployee(EmployeeRequestDTO input);

    void deleteEmployeeById(UUID id);
}
