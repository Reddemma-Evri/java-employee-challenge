package com.reliaquest.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.dto.DeleteEmployeeRequestDTO;
import com.reliaquest.api.dto.EmployeeRequestDTO;
import com.reliaquest.api.dto.ErrorDTO;
import com.reliaquest.api.exception.*;
import com.reliaquest.api.model.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestClient;

@Service
@Validated
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
        Locale locale = LocaleContextHolder.getLocale();
        return Objects.requireNonNull(response).data().orElseThrow(() -> {
            String message = messageSource.getMessage("employee.create.failed", new Object[] {}, locale);
            throw new EmployeeFetchException(message);
        });
    }

    @Override
    public Employee getEmployeeById(String id) {
        UUID uuid = UUID.fromString(id);
        return restClient
                .get()
                .uri("/{id}", uuid)
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(), (req, res) -> {
                    String body = StreamUtils.copyToString(res.getBody(), StandardCharsets.UTF_8);
                    ObjectMapper mapper = new ObjectMapper();
                    ErrorDTO error = mapper.readValue(body, ErrorDTO.class);
                    ApiResponse<ErrorDTO> errorResponse = new ApiResponse<>(
                            Optional.ofNullable(error),
                            true,
                            res.getStatusCode().value(),
                            error.error());
                    throw new RemoteApiException(errorResponse);
                })
                .body(EmployeeApiResponse.class)
                .data();
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
        Locale locale = LocaleContextHolder.getLocale();
        return Objects.requireNonNull(response).data().orElseThrow(() -> {
            String message = messageSource.getMessage("employee.create.failed", new Object[] {}, locale);
            throw new EmployeeCreationException(message);
        });
    }

    @Override
    public String deleteEmployeeById(String id) {
        String empName = getEmployeeById(id).name();
        Locale locale = LocaleContextHolder.getLocale();
        List<Employee> empListWithName = getEmployeesByNameSearch(empName);
        if (empListWithName != null && empListWithName.size() > 1) {
            String message = messageSource.getMessage("employee.name.duplicate", new Object[] {empName}, locale);
            throw new EmployeeException(message);
        }
        var body = new DeleteEmployeeRequestDTO(empName);
        ApiResponse<String> response =
                restClient.method(HttpMethod.DELETE).body(body).retrieve().body(new ParameterizedTypeReference<>() {});
        response.data().orElseThrow(() -> {
            String message = messageSource.getMessage("employee.delete.failed", new Object[] {id}, locale);
            throw new EmployeeDeletionException(message);
        });
        return response.status();
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
