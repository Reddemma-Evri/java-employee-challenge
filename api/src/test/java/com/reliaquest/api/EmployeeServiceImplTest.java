package com.reliaquest.api;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.reliaquest.api.dto.DeleteEmployeeRequestDTO;
import com.reliaquest.api.dto.EmployeeRequestDTO;
import com.reliaquest.api.model.ApiResponse;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.service.EmployeeServiceImpl;
import com.reliaquest.api.utils.AppUtils;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClient;

@SuppressWarnings({"unchecked", "rawtypes"})
@SpringBootTest
public class EmployeeServiceImplTest {

    private RestClient restClient;
    private MessageSource messageSource;
    private EmployeeServiceImpl employeeService;

    private final UUID id = UUID.randomUUID();
    private final Employee employee = new Employee(id, "John Doe", 90000, 35, "Architect", "john@example.com");

    @BeforeEach
    void setUp() {
        restClient = mock(RestClient.class);
        messageSource = mock(MessageSource.class);
        employeeService = new EmployeeServiceImpl(restClient, messageSource);
    }

    @Test
    void testGetAllEmployees_shouldReturnListOfEmployees_whenAvailable() {
        RestClient.RequestHeadersUriSpec uriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);
        List<Employee> employeeList = List.of(employee);
        ApiResponse<List<Employee>> apiResponse =
                AppUtils.buildResponse(employeeList, messageSource, false, HttpStatus.OK, "employee.fetch.success");

        when(restClient.get()).thenReturn(uriSpec);
        when(uriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(apiResponse);

        List<Employee> result = employeeService.getAllEmployees();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).name());
    }

    @Test
    void testGetAllEmployees_shouldThrowException_whenResponseIsNull() {
        RestClient.RequestHeadersUriSpec uriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.get()).thenReturn(uriSpec);
        when(uriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(null);

        assertThrows(NullPointerException.class, () -> employeeService.getAllEmployees());
    }

    @Test
    void testGetEmployeeById_shouldReturnEmployee_whenValidIdProvided() {
        RestClient.RequestHeadersUriSpec uriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);
        ApiResponse<Employee> apiResponse =
                AppUtils.buildResponse(employee, messageSource, false, HttpStatus.OK, "employee.fetch.success");

        when(restClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri("/{id}", id)).thenReturn(uriSpec);
        when(uriSpec.retrieve()).thenReturn(responseSpec);
        when(uriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class)))
                .thenReturn(apiResponse.data().get());

        Employee result = employeeService.getEmployeeById(id.toString());

        assertNotNull(result);
        assertEquals("John Doe", result.name());
    }

    @Test
    void testGetEmployeeById_shouldReturnEmployee_whenIdIsValid() {
        UUID id = UUID.randomUUID();
        Employee expectedEmployee = new Employee(id, "John Doe", 75000, 35, "Engineer", "john.doe@example.com");
        ApiResponse<Employee> apiResponse =
                AppUtils.buildResponse(expectedEmployee, messageSource, false, HttpStatus.OK, "employee.fetch.success");
        RestClient.RequestHeadersUriSpec uriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri("/{id}", id)).thenReturn(uriSpec);
        when(uriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class)))
                .thenReturn(apiResponse.data().get());

        Employee result = employeeService.getEmployeeById(id.toString());

        assertNotNull(result);
        assertEquals(expectedEmployee, result);
    }

    @Test
    void testGetEmployeeById_shouldThrowException_whenResponseIsNull() {
        UUID id = UUID.randomUUID();

        RestClient.RequestHeadersUriSpec uriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri("/{id}", id)).thenReturn(uriSpec);
        when(uriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(null);

        assertThrows(NullPointerException.class, () -> employeeService.getEmployeeById(id.toString()));
    }

    @Test
    void testGetEmployeeById_shouldThrowException_whenEmployeeDataIsNull() {
        UUID id = UUID.randomUUID();
        RestClient.RequestHeadersUriSpec uriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri("/{id}", id)).thenReturn(uriSpec);
        when(uriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(null);
        assertNull(employeeService.getEmployeeById(id.toString()));
    }

    @Test
    void testGetEmployeesByNameSearch_shouldReturnMatchingEmployees() {
        Employee e1 = new Employee(UUID.randomUUID(), "Alice Smith", 50000, 30, "Engineer", "alice@example.com");
        Employee e2 = new Employee(UUID.randomUUID(), "Bob Johnson", 60000, 35, "Manager", "bob@example.com");
        RestClient.RequestHeadersUriSpec uriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);
        ApiResponse<List<Employee>> apiResponse =
                AppUtils.buildResponse(List.of(e1, e2), messageSource, false, HttpStatus.OK, "employee.fetch.success");
        when(restClient.get()).thenReturn(uriSpec);
        when(uriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(apiResponse);

        List<Employee> result = employeeService.getEmployeesByNameSearch("Alice");

        assertEquals(1, result.size());
        assertEquals("Alice Smith", result.get(0).name());
    }

    @Test
    void testGetEmployeesByNameSearch_shouldReturnEmptyList_whenNoMatch() {
        Employee e1 = new Employee(UUID.randomUUID(), "Charlie", 45000, 28, "Dev", "charlie@example.com");
        RestClient.RequestHeadersUriSpec uriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);
        var apiResponse =
                AppUtils.buildResponse(List.of(e1), messageSource, false, HttpStatus.OK, "employee.fetch.success");
        when(restClient.get()).thenReturn(uriSpec);
        when(uriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(apiResponse);

        List<Employee> result = employeeService.getEmployeesByNameSearch("NonExistent");

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetEmployeesByNameSearch_shouldHandleEmptyList() {
        RestClient.RequestHeadersUriSpec uriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);
        var apiResponse = AppUtils.buildResponse(
                Collections.emptyList(), messageSource, false, HttpStatus.OK, "employee.fetch.success");
        when(restClient.get()).thenReturn(uriSpec);
        when(uriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(apiResponse);
        List<Employee> result = employeeService.getEmployeesByNameSearch("Test");
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetEmployeesByNameSearch_shouldThrow_whenResponseIsNull() {
        ApiResponse<Employee> apiResponse =
                AppUtils.buildResponse(null, messageSource, false, HttpStatus.OK, "employee.fetch.success");
        RestClient.RequestHeadersUriSpec uriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);
        when(restClient.get()).thenReturn(uriSpec);
        when(uriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(null);
        assertThrows(NullPointerException.class, () -> employeeService.getEmployeesByNameSearch("Alice"));
    }

    @Test
    void testCreateEmployee_shouldReturnCreatedEmployee() {
        UUID id = UUID.randomUUID();
        RestClient.RequestHeadersUriSpec uriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);
        RestClient.RequestBodyUriSpec bodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        EmployeeRequestDTO requestDTO = new EmployeeRequestDTO("Derek", 55000, 29, "Analyst");
        Employee expected = new Employee(id, "Derek", 55000, 29, "Analyst", "derek@example.com");
        ApiResponse<Employee> apiResponse =
                AppUtils.buildResponse(expected, messageSource, false, HttpStatus.OK, "employee.created.success");
        when(restClient.post()).thenReturn(bodyUriSpec);
        when(bodyUriSpec.body(any(EmployeeRequestDTO.class))).thenReturn(bodyUriSpec);
        when(bodyUriSpec.retrieve()).thenReturn(responseSpec);
        when(uriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(apiResponse);
        Employee result = employeeService.createEmployee(requestDTO);

        assertNotNull(result);
        assertEquals("Derek", result.name());
        assertEquals(55000, result.salary());
    }

    @Test
    void testCreateEmployee_shouldThrow_whenResponseIsNull() {
        EmployeeRequestDTO requestDTO = new EmployeeRequestDTO("John", 50000, 30, "Engineer");
        RestClient.RequestBodyUriSpec bodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);
        when(restClient.post()).thenReturn(bodyUriSpec);
        when(bodyUriSpec.body(eq(requestDTO))).thenReturn(bodyUriSpec);
        when(bodyUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(null); // Simulate null response

        assertThrows(NullPointerException.class, () -> employeeService.createEmployee(requestDTO));
    }

    @Test
    void testCreateEmployee_shouldThrow_whenEmployeeDataIsNull() {
        EmployeeRequestDTO requestDTO = new EmployeeRequestDTO("John", 50000, 30, "Engineer");
        Employee expected = new Employee(id, "Derek", 55000, 29, "Analyst", "derek@example.com");
        ApiResponse<Employee> response =
                AppUtils.buildResponse(expected, messageSource, false, HttpStatus.OK, "employee.fetch.success");
        RestClient.RequestBodyUriSpec bodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);
        when(restClient.post()).thenReturn(bodyUriSpec);
        when(bodyUriSpec.body(eq(requestDTO))).thenReturn(bodyUriSpec);
        when(bodyUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(response);
        assert (employeeService.createEmployee(requestDTO).name().equals(expected.name()));
    }

    @Test
    void testDeleteEmployeeById_shouldDelete_whenSingleEmployeeExists() {
        UUID id = UUID.randomUUID();
        String name = "John Doe";
        Employee employeeById = new Employee(id, name, 50000, 30, "Engineer", "john@example.com");
        ApiResponse<Employee> getByIdResponse =
                AppUtils.buildResponse(employeeById, messageSource, false, HttpStatus.OK, "employee.created.success");
        List<Employee> employeeList = List.of(employeeById);
        ApiResponse<List<Employee>> getAllResponse =
                AppUtils.buildResponse(employeeList, messageSource, false, HttpStatus.OK, "employee.created.success");
        ApiResponse<Boolean> deleteResponse =
                AppUtils.buildResponse(true, messageSource, false, HttpStatus.OK, "employee.deleted.success");

        // GET /employee/{id}
        RestClient.RequestHeadersUriSpec uriSpecId = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.ResponseSpec responseSpecId = mock(RestClient.ResponseSpec.class);
        when(restClient.get()).thenReturn(uriSpecId);
        when(uriSpecId.uri("/{id}", id)).thenReturn(uriSpecId);
        when(uriSpecId.retrieve()).thenReturn(responseSpecId);
        when(responseSpecId.body(any(ParameterizedTypeReference.class))).thenReturn(getByIdResponse);

        // GET /employee (all)
        RestClient.RequestHeadersUriSpec uriSpecAll = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.ResponseSpec responseSpecAll = mock(RestClient.ResponseSpec.class);
        when(restClient.get()).thenReturn(uriSpecAll);
        when(uriSpecAll.retrieve()).thenReturn(responseSpecAll);
        when(responseSpecAll.body(any(ParameterizedTypeReference.class))).thenReturn(getAllResponse);

        // DELETE /employee
        RestClient.RequestBodyUriSpec deleteUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.RequestBodySpec deleteBodySpec = mock(RestClient.RequestBodySpec.class);
        RestClient.ResponseSpec deleteResponseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.method(HttpMethod.DELETE)).thenReturn(deleteUriSpec);
        when(deleteUriSpec.body(any(DeleteEmployeeRequestDTO.class))).thenReturn(deleteBodySpec);
        when(deleteBodySpec.retrieve()).thenReturn(deleteResponseSpec);
        when(deleteResponseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(deleteResponse);

        assertThrows(NullPointerException.class, () -> employeeService.deleteEmployeeById(id.toString()));
    }
}
