package com.reliaquest.api.service;

import com.reliaquest.api.dto.DeleteEmployeeRequestDTO;
import com.reliaquest.api.dto.EmployeeRequestDTO;
import com.reliaquest.api.exception.EmployeeException;
import com.reliaquest.api.model.*;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class EmployeeServiceImpl implements IEmployeeService {

    private final RestClient restClient;

    private final MessageSource messageSource;

    public EmployeeServiceImpl(@Qualifier("employeeRestClient") RestClient restClient, MessageSource messageSource) {
        this.restClient = restClient;
        this.messageSource = messageSource;
    }

    @Override
    public List<Employee> getAllEmployees() {
        ApiResponse<List<Employee>> response = restClient.get().retrieve().body(new ParameterizedTypeReference<>() {});
        return Objects.requireNonNull(response).data();
    }

    @Override
    public Employee getEmployeeById(UUID id) {
        ApiResponse<Employee> response =
                restClient.get().uri("/{id}", id).retrieve().body(new ParameterizedTypeReference<>() {});
        return Objects.requireNonNull(response).data();
    }

    @Override
    public List<Employee> getEmployeesByNameSearch(String searchString) {
        return getAllEmployees().stream()
                .filter(emp -> emp.name().toLowerCase().contains(searchString.toLowerCase()))
                .toList();
    }

    @Override
    public Employee createEmployee(EmployeeRequestDTO input) {
        ApiResponse<Employee> response =
                restClient.post().body(input).retrieve().body(new ParameterizedTypeReference<>() {});
        return Objects.requireNonNull(response).data();
    }

    @Override
    public void deleteEmployeeById(UUID id) {
        String empName = getEmployeeById(id).name();
        Locale locale = LocaleContextHolder.getLocale();
        List<Employee> empListWithName = getEmployeesByNameSearch(empName);
        System.out.println("::Name" + empListWithName);
        if (empListWithName != null && empListWithName.size() > 1) {
            String message = messageSource.getMessage("employee.name.duplicate", new Object[] {empName}, locale);
            throw new EmployeeException(message);
        }
        var body = new DeleteEmployeeRequestDTO(empName);
        ApiResponse<Boolean> response =
                restClient.method(HttpMethod.DELETE).body(body).retrieve().body(new ParameterizedTypeReference<>() {});
        if (!Boolean.TRUE.equals(response.data())) {
            String message = messageSource.getMessage("employee.delete.failed", new Object[] {id}, locale);
            throw new EmployeeException(message);
        }
    }

    @Override
    public int getHighestSalary() {
        return getAllEmployees().stream().mapToInt(Employee::salary).max().orElse(0);
    }

    @Override
    public List<String> getTopTenHighestEarningEmployeeNames() {
        return getAllEmployees().stream()
                .sorted((a, b) -> Integer.compare(b.salary(), a.salary()))
                .limit(10)
                .map(Employee::name)
                .toList();
    }
}
