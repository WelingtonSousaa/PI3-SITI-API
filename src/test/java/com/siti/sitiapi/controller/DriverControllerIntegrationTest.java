package com.siti.sitiapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.siti.sitiapi.dto.DriverCreateRequest;
import com.siti.sitiapi.dto.DriverResponse;
import com.siti.sitiapi.exception.BusinessException;
import com.siti.sitiapi.configs.AuthenticationInterceptor;
import com.siti.sitiapi.service.AuthService;
import com.siti.sitiapi.service.DriverService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DriverController.class)
@AutoConfigureMockMvc(addFilters = false)
public class DriverControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DriverService driverService;

    @MockBean
    private AuthService authService;

    @MockBean
    private AuthenticationInterceptor authenticationInterceptor;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws Exception {
        when(authenticationInterceptor.preHandle(any(), any(), any())).thenReturn(true);
    }

    @Test
    void testCreateDriverSuccess() throws Exception {
        DriverCreateRequest request = new DriverCreateRequest();
        request.setIdUser(1L);
        request.setCnhNumber("12345678900");

        DriverResponse response = new DriverResponse();
        response.setId(1L);
        response.setCnhNumber("12345678900");

        when(driverService.createDriver(any(DriverCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/drivers")
                        .requestAttr("role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.cnhNumber").value("12345678900"));
    }

    @Test
    void testCreateDriverForbidden() throws Exception {
        DriverCreateRequest request = new DriverCreateRequest();
        
        mockMvc.perform(post("/drivers")
                        .requestAttr("role", "DRIVE") // Motoristas não podem criar outros motoristas
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetRoutesSuccess() throws Exception {
        when(driverService.getRoutes(anyString())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/driver/routes")
                        .requestAttr("role", "DRIVE")
                        .requestAttr("userActivate", "driver@test.com"))
                .andExpect(status().isOk());
    }
    
    @Test
    void testGetRoutesForbidden() throws Exception {
        mockMvc.perform(get("/driver/routes")
                        .requestAttr("role", "USER")
                        .requestAttr("userActivate", "user@test.com"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUpdateTripStatus() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("status", "Em Andamento");

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("status", "Em Andamento");

        when(driverService.updateTripStatus(anyLong(), any())).thenReturn(result);

        mockMvc.perform(put("/driver/routes/1/status")
                        .requestAttr("role", "DRIVE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Em Andamento"));
    }

    @Test
    void testReportFailureSuccess() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("vehiclePlate", "ABC-1234");
        payload.put("issueType", "Motor");

        Map<String, Object> result = new HashMap<>();
        result.put("status", "Registrado");

        when(driverService.reportFailure(any())).thenReturn(result);

        mockMvc.perform(post("/driver/failures")
                        .requestAttr("role", "DRIVE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("Registrado"));
    }
    @Test
    void testGetProfileSuccess() throws Exception {
        when(driverService.getProfile(anyString())).thenReturn(new HashMap<>());

        mockMvc.perform(get("/driver/profile")
                        .requestAttr("role", "DRIVE")
                        .requestAttr("userActivate", "driver@test.com"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetVehicleSuccess() throws Exception {
        when(driverService.getVehicle(anyString())).thenReturn(new HashMap<>());

        mockMvc.perform(get("/driver/vehicle")
                        .requestAttr("role", "DRIVE")
                        .requestAttr("userActivate", "driver@test.com"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetRoutePassengersSuccess() throws Exception {
        when(driverService.getPassengers(anyLong())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/driver/routes/1/passengers")
                        .requestAttr("role", "DRIVE")
                        .requestAttr("userActivate", "driver@test.com"))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdatePassengerStatusSuccess() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("status", "Embarcou");

        when(driverService.updatePassengerStatus(anyLong(), any())).thenReturn(payload);

        mockMvc.perform(put("/driver/passengers/1/status")
                        .requestAttr("role", "DRIVE")
                        .requestAttr("userActivate", "driver@test.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk());
    }
}
