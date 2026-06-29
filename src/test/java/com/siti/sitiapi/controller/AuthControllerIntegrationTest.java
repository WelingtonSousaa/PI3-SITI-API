package com.siti.sitiapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.siti.sitiapi.dto.LoginRequest;
import com.siti.sitiapi.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.siti.sitiapi.configs.AuthenticationInterceptor;
import org.junit.jupiter.api.BeforeEach;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // Desativa filtros de segurança Spring Security (se houver interceptors globais não mockados)
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

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
    void testAuthenticateSuccess() throws Exception {
        Map<String, Object> mockProfile = new HashMap<>();
        mockProfile.put("name", "Test User");
        mockProfile.put("role", "USER");

        when(authService.getUserProfileByEmail("test@test.com")).thenReturn(mockProfile);

        mockMvc.perform(get("/auth/authenticate")
                        .requestAttr("userActivate", "test@test.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void testAuthenticateFailure() throws Exception {
        when(authService.getUserProfileByEmail("unknown@test.com")).thenThrow(new RuntimeException("Usuário não encontrado."));

        mockMvc.perform(get("/auth/authenticate")
                        .requestAttr("userActivate", "unknown@test.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.erro").value("Usuário não encontrado."));
    }

    @Test
    void testLoginSuccess() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@test.com");
        request.setPassword("password123");

        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("token", "fake-token");
        mockResponse.put("role", "USER");

        when(authService.login(request.getEmail(), request.getPassword())).thenReturn(mockResponse);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-token"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void testLoginFailure() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@test.com");
        request.setPassword("wrongpassword");

        when(authService.login(request.getEmail(), request.getPassword())).thenThrow(new RuntimeException("Usuário ou senha incorretos."));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.erro").value("Usuário ou senha incorretos."));
    }

    @Test
    void testForgotPasswordSuccess() throws Exception {
        Map<String, String> payload = new HashMap<>();
        payload.put("email", "test@test.com");

        mockMvc.perform(post("/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testForgotPasswordFailure() throws Exception {
        Map<String, String> payload = new HashMap<>();
        payload.put("email", "unknown@test.com");

        doThrow(new RuntimeException("E-mail não encontrado")).when(authService).forgotPassword("unknown@test.com");

        mockMvc.perform(post("/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("E-mail não encontrado"));
    }

    @Test
    void testResetPasswordSuccess() throws Exception {
        Map<String, String> payload = new HashMap<>();
        payload.put("token", "valid-token");
        payload.put("newPassword", "newpass123");

        mockMvc.perform(post("/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testResetPasswordFailure() throws Exception {
        Map<String, String> payload = new HashMap<>();
        payload.put("token", "invalid-token");
        payload.put("newPassword", "newpass");

        doThrow(new RuntimeException("Token expirado ou inválido.")).when(authService).resetPassword("invalid-token", "newpass");

        mockMvc.perform(post("/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Token expirado ou inválido."));
    }
}
