package com.siti.sitiapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.siti.sitiapi.dto.PassengerCreateRequest;
import com.siti.sitiapi.dto.PassengerResponse;
import com.siti.sitiapi.exception.BusinessException;
import com.siti.sitiapi.dto.ErrorResponse;
import com.siti.sitiapi.configs.AuthenticationInterceptor;
import com.siti.sitiapi.service.AuthService;
import com.siti.sitiapi.service.PassengerService;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PassengerController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PassengerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PassengerService passengerService;

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
    void testCreateSuccess() throws Exception {
        PassengerCreateRequest request = new PassengerCreateRequest();
        request.setIdUser(1L);
        request.setRegistrationNumber("12345");

        PassengerResponse response = new PassengerResponse();
        response.setId(1L);
        response.setRegistrationNumber("12345");
        response.setEmail("user@test.com");

        when(passengerService.create(any(PassengerCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/passengers/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("user@test.com"));
    }

    @Test
    void testCreateFailure() throws Exception {
        PassengerCreateRequest request = new PassengerCreateRequest();
        request.setIdUser(1L);

        when(passengerService.create(any(PassengerCreateRequest.class)))
                .thenThrow(new BusinessException(new ErrorResponse(400, "Usuário não encontrado.", "/passengers/create")));

        mockMvc.perform(post("/passengers/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetRoutesForbidden() throws Exception {
        mockMvc.perform(get("/passenger/routes")
                        .requestAttr("role", "DRIVE")) // Drivers cannot access passenger routes endpoint
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetRoutesSuccess() throws Exception {
        when(passengerService.getRoutes()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/passenger/routes")
                        .requestAttr("role", "USER"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetProfileForbidden() throws Exception {
        mockMvc.perform(get("/passenger/profile")
                        .requestAttr("role", "DRIVE")
                        .requestAttr("userActivate", "test@test.com"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetProfileSuccess() throws Exception {
        Map<String, Object> profile = new HashMap<>();
        profile.put("name", "John Doe");
        
        when(passengerService.getProfile(anyString())).thenReturn(profile);

        mockMvc.perform(get("/passenger/profile")
                        .requestAttr("role", "USER")
                        .requestAttr("userActivate", "test@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"));
    }
    
    @Test
    void testVoteSuccess() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("routeId", 1);
        
        when(passengerService.vote(anyString(), any())).thenReturn(Map.of("success", true));

        mockMvc.perform(post("/passenger/votes")
                        .requestAttr("role", "USER")
                        .requestAttr("userActivate", "test@test.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
    
    @Test
    void testVoteForbidden() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        mockMvc.perform(post("/passenger/votes")
                        .requestAttr("role", "DRIVE")
                        .requestAttr("userActivate", "test@test.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isForbidden());
    }
    @Test
    void testGetNoticesSuccess() throws Exception {
        when(passengerService.getNotices()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/passenger/notices")
                        .requestAttr("role", "USER")
                        .requestAttr("userActivate", "test@test.com"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetContactsSuccess() throws Exception {
        when(passengerService.getContacts(anyString())).thenReturn(new HashMap<>());

        mockMvc.perform(get("/passenger/contacts")
                        .requestAttr("role", "USER")
                        .requestAttr("userActivate", "test@test.com"))
                .andExpect(status().isOk());
    }

    @Test
    void testSupportSuccess() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("message", "Help");

        when(passengerService.submitSupport(anyString(), any())).thenReturn(Map.of("success", true));

        mockMvc.perform(post("/passenger/support")
                        .requestAttr("role", "USER")
                        .requestAttr("userActivate", "test@test.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdatePhotoSuccess() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("photoUrl", "http://image.com");

        when(passengerService.uploadPhoto(anyString())).thenReturn(Map.of("success", true));

        mockMvc.perform(post("/passenger/photo")
                        .requestAttr("role", "USER")
                        .requestAttr("userActivate", "test@test.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk());
    }
}
