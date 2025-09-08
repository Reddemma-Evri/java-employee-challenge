package com.reliaquest.api.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.reliaquest.api.model.Employee;
import com.reliaquest.api.service.IEmployeeService;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(EmployeeControllerImpl.class)
class EmployeeControllerImplTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IEmployeeService employeeService;

    @Test
    void testGetEmployeeById_shouldReturnEmployee() throws Exception {
        UUID id = UUID.randomUUID();
        String idStr = id.toString();
        Employee employee = new Employee(id, "John Doe", 50000, 30, "Engineer", "john@example.com");
        when(employeeService.getEmployeeById(id.toString())).thenReturn(employee);

        mockMvc.perform(get("/" + idStr)) // <-- use actual URL
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employee_name").value("John Doe"));
    }
}
