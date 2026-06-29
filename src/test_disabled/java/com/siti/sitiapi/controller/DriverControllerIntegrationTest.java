package com.siti.sitiapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DriverControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final String MOCK_ADMIN_TOKEN = "mock-jwt-token-admin@siti.edu.br"; 
    private final String MOCK_DRIVER_TOKEN = "mock-jwt-token-carlos@siti.com"; // carlos corresponds to DRIVE role
    private final String MOCK_USER_TOKEN = "mock-jwt-token-mariana@siti.com"; // mariana corresponds to USER role

    // ============================================
    // /drivers
    // ============================================

    @Test
    void testCreateDriverSuccess() throws Exception {
        org.springframework.jdbc.core.JdbcTemplate jdbc = new org.springframework.jdbc.core.JdbcTemplate(mockMvc.getDispatcherServlet().getWebApplicationContext().getBean(javax.sql.DataSource.class));
        jdbc.execute("INSERT INTO users (email, password, status, identifier_document, name) VALUES ('newdriver@siti.edu.br', '123456', 'Pendente', '111', 'Driver')");
        Long pendingId = jdbc.queryForObject("SELECT id FROM users WHERE email='newdriver@siti.edu.br'", Long.class);

        Map<String, Object> payload = new HashMap<>();
        payload.put("id", pendingId);
        payload.put("cnhNumber", "987654321");
        payload.put("cnhCategory", "D");
        payload.put("name", "Motorista Silva");
        payload.put("birthDate", "1980-05-15");
        payload.put("cnhValidityDate", "2030-01-01");
        payload.put("phone", "85988887777");
        payload.put("idAddress", 1L);

        mockMvc.perform(post("/drivers")
                .header("Authorization", "Bearer " + MOCK_ADMIN_TOKEN)
                .header("Role", "ADMIN") // ONLY Admin can create drivers
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateDriverForbidden() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        mockMvc.perform(post("/drivers")
                .header("Authorization", "Bearer " + MOCK_DRIVER_TOKEN)
                .header("Role", "DRIVE") // Drive cannot create driver
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isForbidden());
    }

    // ============================================
    // /driver/routes
    // ============================================

    @Test
    void testGetRoutesSuccess() throws Exception {
        mockMvc.perform(get("/driver/routes")
                .header("Authorization", "Bearer " + MOCK_DRIVER_TOKEN)
                .header("Role", "DRIVE"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetRoutesForbidden() throws Exception {
        mockMvc.perform(get("/driver/routes")
                .header("Authorization", "Bearer " + MOCK_USER_TOKEN)
                .header("Role", "USER"))
                .andExpect(status().isForbidden());
    }

    // ============================================
    // /driver/profile
    // ============================================

    @Test
    void testGetProfileSuccess() throws Exception {
        mockMvc.perform(get("/driver/profile")
                .header("Authorization", "Bearer " + MOCK_DRIVER_TOKEN)
                .header("Role", "DRIVE"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetProfileForbidden() throws Exception {
        mockMvc.perform(get("/driver/profile")
                .header("Authorization", "Bearer " + MOCK_USER_TOKEN)
                .header("Role", "USER"))
                .andExpect(status().isForbidden());
    }

    // ============================================
    // /driver/vehicle
    // ============================================

    @Test
    void testGetVehicleSuccess() throws Exception {
        mockMvc.perform(get("/driver/vehicle")
                .header("Authorization", "Bearer " + MOCK_DRIVER_TOKEN)
                .header("Role", "DRIVE"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetVehicleForbidden() throws Exception {
        mockMvc.perform(get("/driver/vehicle")
                .header("Authorization", "Bearer " + MOCK_USER_TOKEN)
                .header("Role", "USER"))
                .andExpect(status().isForbidden());
    }

    // ============================================
    // /driver/routes/{id}/status
    // ============================================

    @Test
    void testUpdateTripStatusSuccess() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("status", "Em Andamento");

        mockMvc.perform(put("/driver/routes/1/status")
                .header("Authorization", "Bearer " + MOCK_DRIVER_TOKEN)
                .header("Role", "DRIVE")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateTripStatusForbidden() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        mockMvc.perform(put("/driver/routes/1/status")
                .header("Authorization", "Bearer " + MOCK_USER_TOKEN)
                .header("Role", "USER")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isForbidden());
    }

    // ============================================
    // /driver/routes/{routeId}/passengers
    // ============================================

    @Test
    void testGetPassengersSuccess() throws Exception {
        mockMvc.perform(get("/driver/routes/1/passengers")
                .header("Authorization", "Bearer " + MOCK_DRIVER_TOKEN)
                .header("Role", "DRIVE"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetPassengersForbidden() throws Exception {
        mockMvc.perform(get("/driver/routes/1/passengers")
                .header("Authorization", "Bearer " + MOCK_USER_TOKEN)
                .header("Role", "USER"))
                .andExpect(status().isForbidden());
    }

    // ============================================
    // /driver/passengers/{passengerId}/status
    // ============================================

    @Test
    void testUpdatePassengerStatusSuccess() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("status", "Embarcado");

        mockMvc.perform(put("/driver/passengers/1/status")
                .header("Authorization", "Bearer " + MOCK_DRIVER_TOKEN)
                .header("Role", "DRIVE")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdatePassengerStatusForbidden() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        mockMvc.perform(put("/driver/passengers/1/status")
                .header("Authorization", "Bearer " + MOCK_USER_TOKEN)
                .header("Role", "USER")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isForbidden());
    }

    // ============================================
    // /driver/failures
    // ============================================

    @Test
    void testReportFailureSuccess() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("vehiclePlate", "ABC-1234");
        payload.put("issueType", "Mecânico");
        payload.put("severity", "Alta");
        payload.put("description", "Pneu furado");

        mockMvc.perform(post("/driver/failures") 
                .header("Authorization", "Bearer " + MOCK_DRIVER_TOKEN)
                .header("Role", "DRIVE")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated());
    }

    @Test
    void testReportFailureForbidden() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        mockMvc.perform(post("/driver/failures")
                .header("Authorization", "Bearer " + MOCK_USER_TOKEN)
                .header("Role", "USER")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isForbidden());
    }
}
