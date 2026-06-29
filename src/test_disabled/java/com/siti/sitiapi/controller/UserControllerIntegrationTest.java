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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // ============================================
    // /users/register
    // ============================================

    @Test
    void testRegisterUserSuccess() throws Exception {
        Map<String, String> payload = new HashMap<>();
        payload.put("email", "newuser@siti.edu.br");
        payload.put("password", "strongpass123");
        payload.put("identifierDocument", "123456789");

        mockMvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testRegisterUserFailureDuplicateEmail() throws Exception {
        org.springframework.jdbc.core.JdbcTemplate jdbc = new org.springframework.jdbc.core.JdbcTemplate(mockMvc.getDispatcherServlet().getWebApplicationContext().getBean(javax.sql.DataSource.class));
        try {
            jdbc.execute("INSERT INTO users (email, password, status, identifier_document, name) VALUES ('admin@siti.edu.br', '123456', 'Ativo', '000000', 'Admin')");
        } catch (Exception e) {}

        Map<String, String> payload = new HashMap<>();
        payload.put("email", "admin@siti.edu.br"); // Already exists
        payload.put("password", "strongpass123");
        payload.put("identifierDocument", "123456789");

        mockMvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email já utilizado!"));
    }

    // ============================================
    // /users/admin/register
    // ============================================

    @Test
    void testRegisterAdminSuccess() throws Exception {
        Map<String, String> payload = new HashMap<>();
        payload.put("email", "newadmin@siti.edu.br");
        payload.put("password", "strongpass123");
        payload.put("cnpj", "12.345.678/0001-90");
        payload.put("companyName", "Viação SITI");
        payload.put("city", "Fortaleza");
        payload.put("state", "CE");

        mockMvc.perform(post("/users/admin/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testRegisterAdminFailureDuplicateEmail() throws Exception {
        org.springframework.jdbc.core.JdbcTemplate jdbc = new org.springframework.jdbc.core.JdbcTemplate(mockMvc.getDispatcherServlet().getWebApplicationContext().getBean(javax.sql.DataSource.class));
        try {
            jdbc.execute("INSERT INTO users (email, password, status, identifier_document, name) VALUES ('admin@siti.edu.br', '123456', 'Ativo', '000000', 'Admin')");
        } catch (Exception e) {}

        Map<String, String> payload = new HashMap<>();
        payload.put("email", "admin@siti.edu.br"); // Already exists
        payload.put("password", "strongpass123");
        payload.put("cnpj", "12.345.678/0001-90");
        payload.put("companyName", "Viação SITI");
        payload.put("city", "Fortaleza");
        payload.put("state", "CE");

        mockMvc.perform(post("/users/admin/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email já utilizado!"));
    }
}
