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
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // ============================================
    // /auth/login
    // ============================================

    @Test
    void testLoginSuccess() throws Exception {
        Map<String, String> payload = new HashMap<>();
        payload.put("email", "admin@siti.edu.br");
        payload.put("password", "123456");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void testLoginFailureInvalidCredentials() throws Exception {
        Map<String, String> payload = new HashMap<>();
        payload.put("email", "admin@siti.edu.br");
        payload.put("password", "wrongpass");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.erro").exists());
    }

    // ============================================
    // /auth/authenticate
    // ============================================

    @Test
    void testAuthenticateSuccess() throws Exception {
        // Needs a mock token generated for this test.
        // We will use the mock interceptor logic setup:
        String token = "mock-jwt-token-admin@siti.edu.br";

        mockMvc.perform(get("/auth/authenticate")
                .header("Authorization", "Bearer " + token)
                .header("Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("admin@siti.edu.br"));
    }

    @Test
    void testAuthenticateFailureMissingToken() throws Exception {
        mockMvc.perform(get("/auth/authenticate"))
                .andExpect(status().isUnauthorized()); // The interceptor will block it
    }

    // ============================================
    // /auth/forgot-password
    // ============================================

    @Test
    void testForgotPasswordSuccess() throws Exception {
        Map<String, String> payload = new HashMap<>();
        payload.put("email", "admin@siti.edu.br");

        mockMvc.perform(post("/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testForgotPasswordFailureUserNotFound() throws Exception {
        Map<String, String> payload = new HashMap<>();
        payload.put("email", "notfound@siti.edu.br");

        mockMvc.perform(post("/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    // ============================================
    // /auth/reset-password
    // ============================================

    @Test
    void testResetPasswordFailureInvalidToken() throws Exception {
        Map<String, String> payload = new HashMap<>();
        payload.put("token", "invalid-uuid-token");
        payload.put("newPassword", "newpass123");

        mockMvc.perform(post("/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }
}
