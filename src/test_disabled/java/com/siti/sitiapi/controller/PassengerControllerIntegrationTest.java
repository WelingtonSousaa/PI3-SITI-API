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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PassengerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final String MOCK_USER_TOKEN = "mock-jwt-token-mariana@siti.com"; // mariana corresponds to USER role

    // ============================================
    // /passengers/create
    // ============================================

    @Test
    void testCreatePassengerSuccess() throws Exception {
        org.springframework.jdbc.core.JdbcTemplate jdbc = new org.springframework.jdbc.core.JdbcTemplate(mockMvc.getDispatcherServlet().getWebApplicationContext().getBean(javax.sql.DataSource.class));
        jdbc.execute("INSERT INTO users (email, password, status, identifier_document, name) VALUES ('newpass@siti.edu.br', '123456', 'Pendente', '111', 'Passenger')");
        Long pendingId = jdbc.queryForObject("SELECT id FROM users WHERE email='newpass@siti.edu.br'", Long.class);

        Map<String, Object> payload = new HashMap<>();
        payload.put("id", pendingId);
        payload.put("birthDate", "2000-01-01");
        payload.put("phone", "11999999999");
        payload.put("type", "Estudante");
        payload.put("registrationNumber", "123456");
        payload.put("bondProof", "url_to_proof");
        payload.put("idAddress", 1L);

        mockMvc.perform(post("/passengers/create")
                .header("Authorization", "Bearer " + MOCK_USER_TOKEN)
                .header("Role", "USER")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk());
    }

    @Test
    void testCreatePassengerFailure() throws Exception {
        // Missing required ID should trigger failure
        Map<String, Object> payload = new HashMap<>();
        mockMvc.perform(post("/passengers/create")
                .header("Authorization", "Bearer " + MOCK_USER_TOKEN)
                .header("Role", "USER")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());
    }

    // ============================================
    // /passenger/routes
    // ============================================

    @Test
    void testGetRoutesSuccess() throws Exception {
        mockMvc.perform(get("/passenger/routes")
                .header("Authorization", "Bearer " + MOCK_USER_TOKEN)
                .header("Role", "USER"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetRoutesForbidden() throws Exception {
        mockMvc.perform(get("/passenger/routes")
                .header("Authorization", "Bearer " + MOCK_USER_TOKEN)
                .header("Role", "DRIVER")) // Only USER or ADMIN can access
                .andExpect(status().isForbidden());
    }

    // ============================================
    // /passenger/profile
    // ============================================

    @Test
    void testGetProfileSuccess() throws Exception {
        mockMvc.perform(get("/passenger/profile")
                .header("Authorization", "Bearer " + MOCK_USER_TOKEN)
                .header("Role", "USER"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetProfileForbidden() throws Exception {
        mockMvc.perform(get("/passenger/profile")
                .header("Authorization", "Bearer " + MOCK_USER_TOKEN)
                .header("Role", "DRIVER"))
                .andExpect(status().isForbidden());
    }

    // ============================================
    // /passenger/votes
    // ============================================

    @Test
    void testVoteSuccess() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("routeId", 1L);
        payload.put("stopName", "Parada Central");

        mockMvc.perform(post("/passenger/votes")
                .header("Authorization", "Bearer " + MOCK_USER_TOKEN)
                .header("Role", "USER")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk());
    }

    @Test
    void testVoteForbidden() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        mockMvc.perform(post("/passenger/votes")
                .header("Authorization", "Bearer " + MOCK_USER_TOKEN)
                .header("Role", "DRIVER")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isForbidden());
    }

    // ============================================
    // /passenger/notices
    // ============================================

    @Test
    void testGetNoticesSuccess() throws Exception {
        mockMvc.perform(get("/passenger/notices")
                .header("Authorization", "Bearer " + MOCK_USER_TOKEN)
                .header("Role", "USER"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetNoticesForbidden() throws Exception {
        mockMvc.perform(get("/passenger/notices")
                .header("Authorization", "Bearer " + MOCK_USER_TOKEN)
                .header("Role", "DRIVER"))
                .andExpect(status().isForbidden());
    }

    // ============================================
    // /passenger/contacts
    // ============================================

    @Test
    void testGetContactsSuccess() throws Exception {
        mockMvc.perform(get("/passenger/contacts")
                .header("Authorization", "Bearer " + MOCK_USER_TOKEN)
                .header("Role", "USER"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetContactsForbidden() throws Exception {
        mockMvc.perform(get("/passenger/contacts")
                .header("Authorization", "Bearer " + MOCK_USER_TOKEN)
                .header("Role", "DRIVER"))
                .andExpect(status().isForbidden());
    }

    // ============================================
    // /passenger/support
    // ============================================

    @Test
    void testSubmitSupportSuccess() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("subject", "Problema");
        payload.put("message", "App travando");

        mockMvc.perform(post("/passenger/support")
                .header("Authorization", "Bearer " + MOCK_USER_TOKEN)
                .header("Role", "USER")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk());
    }

    @Test
    void testSubmitSupportForbidden() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        mockMvc.perform(post("/passenger/support")
                .header("Authorization", "Bearer " + MOCK_USER_TOKEN)
                .header("Role", "DRIVER")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isForbidden());
    }

    // ============================================
    // /passenger/photo
    // ============================================

    @Test
    void testUploadPhotoSuccess() throws Exception {
        mockMvc.perform(post("/passenger/photo")
                .header("Authorization", "Bearer " + MOCK_USER_TOKEN)
                .header("Role", "USER"))
                .andExpect(status().isOk());
    }

    @Test
    void testUploadPhotoForbidden() throws Exception {
        mockMvc.perform(post("/passenger/photo")
                .header("Authorization", "Bearer " + MOCK_USER_TOKEN)
                .header("Role", "DRIVER"))
                .andExpect(status().isForbidden());
    }
}
