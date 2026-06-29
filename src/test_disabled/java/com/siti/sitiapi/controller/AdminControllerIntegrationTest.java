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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final String MOCK_TOKEN = "mock-jwt-token-admin@siti.edu.br";

    // ============================================
    // /admin/pending-homologations
    // ============================================

    @Test
    void testGetPendingHomologationsSuccess() throws Exception {
        mockMvc.perform(get("/admin/pending-homologations")
                .header("Authorization", "Bearer " + MOCK_TOKEN)
                .header("Role", "ADMIN"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetPendingHomologationsForbidden() throws Exception {
        mockMvc.perform(get("/admin/pending-homologations")
                .header("Authorization", "Bearer " + MOCK_TOKEN)
                .header("Role", "USER"))
                .andExpect(status().isForbidden());
    }

    // ============================================
    // /admin/homologate/{id}
    // ============================================

    @Test
    void testHomologateSuccess() throws Exception {
        org.springframework.jdbc.core.JdbcTemplate jdbc = new org.springframework.jdbc.core.JdbcTemplate(mockMvc.getDispatcherServlet().getWebApplicationContext().getBean(javax.sql.DataSource.class));
        jdbc.execute("INSERT INTO users (email, password, status, identifier_document, name) VALUES ('pending@siti.edu.br', '123456', 'Pendente', '111', 'Pending')");
        Long pendingId = jdbc.queryForObject("SELECT id FROM users WHERE email='pending@siti.edu.br'", Long.class);

        mockMvc.perform(post("/admin/homologate/" + pendingId)
                .header("Authorization", "Bearer " + MOCK_TOKEN)
                .header("Role", "ADMIN"))
                .andExpect(status().isOk());
    }

    @Test
    void testHomologateForbidden() throws Exception {
        mockMvc.perform(post("/admin/homologate/1")
                .header("Authorization", "Bearer " + MOCK_TOKEN)
                .header("Role", "USER"))
                .andExpect(status().isForbidden());
    }

    // ============================================
    // /admin/reject/{id}
    // ============================================

    @Test
    void testRejectSuccess() throws Exception {
        mockMvc.perform(post("/admin/reject/2")
                .header("Authorization", "Bearer " + MOCK_TOKEN)
                .header("Role", "ADMIN"))
                .andExpect(status().isOk());
    }

    @Test
    void testRejectForbidden() throws Exception {
        mockMvc.perform(post("/admin/reject/2")
                .header("Authorization", "Bearer " + MOCK_TOKEN)
                .header("Role", "USER"))
                .andExpect(status().isForbidden());
    }

    // ============================================
    // /admin/passengers
    // ============================================

    @Test
    void testGetPassengersSuccess() throws Exception {
        mockMvc.perform(get("/admin/passengers")
                .header("Authorization", "Bearer " + MOCK_TOKEN)
                .header("Role", "ADMIN"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetPassengersForbidden() throws Exception {
        mockMvc.perform(get("/admin/passengers")
                .header("Authorization", "Bearer " + MOCK_TOKEN)
                .header("Role", "USER"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreatePassengerSuccess() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("name", "Passageiro Teste");
        payload.put("email", "passageiro_admin_test@siti.edu.br");
        payload.put("password", "123456");

        mockMvc.perform(post("/admin/passengers")
                .header("Authorization", "Bearer " + MOCK_TOKEN)
                .header("Role", "ADMIN")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated()); // Note: if mock service returns empty, it's 201 Created
    }

    @Test
    void testCreatePassengerForbidden() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        mockMvc.perform(post("/admin/passengers")
                .header("Authorization", "Bearer " + MOCK_TOKEN)
                .header("Role", "USER")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUpdatePassengerSuccess() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("name", "Passageiro Atualizado");

        mockMvc.perform(put("/admin/passengers/1")
                .header("Authorization", "Bearer " + MOCK_TOKEN)
                .header("Role", "ADMIN")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdatePassengerForbidden() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        mockMvc.perform(put("/admin/passengers/1")
                .header("Authorization", "Bearer " + MOCK_TOKEN)
                .header("Role", "USER")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeletePassengerSuccess() throws Exception {
        mockMvc.perform(delete("/admin/passengers/1")
                .header("Authorization", "Bearer " + MOCK_TOKEN)
                .header("Role", "ADMIN"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeletePassengerForbidden() throws Exception {
        mockMvc.perform(delete("/admin/passengers/1")
                .header("Authorization", "Bearer " + MOCK_TOKEN)
                .header("Role", "USER"))
                .andExpect(status().isForbidden());
    }

    // ============================================
    // /admin/routes
    // ============================================

    @Test
    void testGetRoutesSuccess() throws Exception {
        mockMvc.perform(get("/admin/routes")
                .header("Authorization", "Bearer " + MOCK_TOKEN)
                .header("Role", "ADMIN"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetRoutesForbidden() throws Exception {
        mockMvc.perform(get("/admin/routes")
                .header("Authorization", "Bearer " + MOCK_TOKEN)
                .header("Role", "USER"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreateRouteSuccess() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("code", "R-100");
        payload.put("name", "Nova Rota");
        payload.put("description", "Descricao da Rota");

        mockMvc.perform(post("/admin/routes")
                .header("Authorization", "Bearer " + MOCK_TOKEN)
                .header("Role", "ADMIN")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated());
    }

    @Test
    void testCreateRouteForbidden() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        mockMvc.perform(post("/admin/routes")
                .header("Authorization", "Bearer " + MOCK_TOKEN)
                .header("Role", "USER")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isForbidden());
    }

    // ============================================
    // /admin/vehicles
    // ============================================

    @Test
    void testGetVehiclesSuccess() throws Exception {
        mockMvc.perform(get("/admin/vehicles")
                .header("Authorization", "Bearer " + MOCK_TOKEN)
                .header("Role", "ADMIN"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetVehiclesForbidden() throws Exception {
        mockMvc.perform(get("/admin/vehicles")
                .header("Authorization", "Bearer " + MOCK_TOKEN)
                .header("Role", "USER"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreateVehicleSuccess() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("plate", "XYZ-9876");
        payload.put("model", "Volks");
        payload.put("year", "2020");
        payload.put("capacity", 40);
        payload.put("accessibility", "Sim");

        mockMvc.perform(post("/admin/vehicles")
                .header("Authorization", "Bearer " + MOCK_TOKEN)
                .header("Role", "ADMIN")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated());
    }

    @Test
    void testCreateVehicleForbidden() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        mockMvc.perform(post("/admin/vehicles")
                .header("Authorization", "Bearer " + MOCK_TOKEN)
                .header("Role", "USER")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isForbidden());
    }

    // ============================================
    // /admin/drivers
    // ============================================

    @Test
    void testGetDriversSuccess() throws Exception {
        mockMvc.perform(get("/admin/drivers")
                .header("Authorization", "Bearer " + MOCK_TOKEN)
                .header("Role", "ADMIN"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetDriversForbidden() throws Exception {
        mockMvc.perform(get("/admin/drivers")
                .header("Authorization", "Bearer " + MOCK_TOKEN)
                .header("Role", "USER"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreateDriverSuccess() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("name", "Motorista de Teste");
        payload.put("cnh", "12345678901");
        payload.put("category", "D");
        payload.put("birthDate", "1990-01-01");
        payload.put("validity", "2030-01-01");
        payload.put("phone", "(88) 99999-8888");
        payload.put("email", "driver_test@siti.edu.br");

        mockMvc.perform(post("/admin/drivers")
                .header("Authorization", "Bearer " + MOCK_TOKEN)
                .header("Role", "ADMIN")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated());
    }

    @Test
    void testCreateDriverForbidden() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        mockMvc.perform(post("/admin/drivers")
                .header("Authorization", "Bearer " + MOCK_TOKEN)
                .header("Role", "USER")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isForbidden());
    }

    // ============================================
    // /admin/settings
    // ============================================

    @Test
    void testGetSettingsSuccess() throws Exception {
        mockMvc.perform(get("/admin/settings")
                .header("Authorization", "Bearer " + MOCK_TOKEN)
                .header("Role", "ADMIN"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetSettingsForbidden() throws Exception {
        mockMvc.perform(get("/admin/settings")
                .header("Authorization", "Bearer " + MOCK_TOKEN)
                .header("Role", "USER"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUpdateSettingsSuccess() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("openTime", "08:00");
        payload.put("closeTime", "18:00");
        payload.put("blockedNextDay", false);

        mockMvc.perform(put("/admin/settings")
                .header("Authorization", "Bearer " + MOCK_TOKEN)
                .header("Role", "ADMIN")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateSettingsForbidden() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        mockMvc.perform(put("/admin/settings")
                .header("Authorization", "Bearer " + MOCK_TOKEN)
                .header("Role", "USER")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isForbidden());
    }

    // ============================================
    // /admin/notices
    // ============================================

    @Test
    void testCreateNoticeSuccess() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("title", "Aviso");
        payload.put("message", "Conteudo do aviso");

        mockMvc.perform(post("/admin/notices")
                .header("Authorization", "Bearer " + MOCK_TOKEN)
                .header("Role", "ADMIN")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated());
    }

    @Test
    void testCreateNoticeForbidden() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        mockMvc.perform(post("/admin/notices")
                .header("Authorization", "Bearer " + MOCK_TOKEN)
                .header("Role", "USER")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isForbidden());
    }

    // ============================================
    // /admin/support-messages
    // ============================================

    @Test
    void testGetSupportMessagesSuccess() throws Exception {
        mockMvc.perform(get("/admin/support-messages")
                .header("Authorization", "Bearer " + MOCK_TOKEN)
                .header("Role", "ADMIN"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetSupportMessagesForbidden() throws Exception {
        mockMvc.perform(get("/admin/support-messages")
                .header("Authorization", "Bearer " + MOCK_TOKEN)
                .header("Role", "USER"))
                .andExpect(status().isForbidden());
    }

    // ============================================
    // /admin/settings/block-voting
    // ============================================

    @Test
    void testBlockVotingSuccess() throws Exception {
        Map<String, Boolean> payload = new HashMap<>();
        payload.put("block", true);

        mockMvc.perform(post("/admin/settings/block-voting")
                .header("Authorization", "Bearer " + MOCK_TOKEN)
                .header("Role", "ADMIN")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk());
    }

    @Test
    void testBlockVotingForbidden() throws Exception {
        Map<String, Boolean> payload = new HashMap<>();
        mockMvc.perform(post("/admin/settings/block-voting")
                .header("Authorization", "Bearer " + MOCK_TOKEN)
                .header("Role", "USER")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isForbidden());
    }

    // ============================================
    // /admin/reports/passengers
    // ============================================

    @Test
    void testGetPassengerReportsSuccess() throws Exception {
        mockMvc.perform(get("/admin/reports/passengers")
                .header("Authorization", "Bearer " + MOCK_TOKEN)
                .header("Role", "ADMIN"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetPassengerReportsForbidden() throws Exception {
        mockMvc.perform(get("/admin/reports/passengers")
                .header("Authorization", "Bearer " + MOCK_TOKEN)
                .header("Role", "USER"))
                .andExpect(status().isForbidden());
    }

    // ============================================
    // /admin/routes/{routeId}/replace-vehicle
    // ============================================

    @Test
    void testReplaceVehicleSuccess() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("newBusId", 2);

        mockMvc.perform(put("/admin/routes/1/replace-vehicle")
                .header("Authorization", "Bearer " + MOCK_TOKEN)
                .header("Role", "ADMIN")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk());
    }

    @Test
    void testReplaceVehicleForbidden() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        mockMvc.perform(put("/admin/routes/1/replace-vehicle")
                .header("Authorization", "Bearer " + MOCK_TOKEN)
                .header("Role", "USER")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isForbidden());
    }
}
