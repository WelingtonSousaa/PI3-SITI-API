package com.siti.sitiapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.siti.sitiapi.configs.AuthenticationInterceptor;
import com.siti.sitiapi.service.AuthService;
import com.siti.sitiapi.service.AdminService;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdminControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

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
    void testGetPendingHomologationsForbidden() throws Exception {
        mockMvc.perform(get("/admin/pending-homologations")
                        .requestAttr("role", "USER"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetPendingHomologationsSuccess() throws Exception {
        when(adminService.getPendingHomologations()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/admin/pending-homologations")
                        .requestAttr("role", "ADMIN"))
                .andExpect(status().isOk());
    }

    @Test
    void testHomologateForbidden() throws Exception {
        mockMvc.perform(post("/admin/homologate/1")
                        .requestAttr("role", "DRIVE"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testHomologateSuccess() throws Exception {
        doNothing().when(adminService).homologate(1L);

        mockMvc.perform(post("/admin/homologate/1")
                        .requestAttr("role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testCreatePassengerSuccess() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("name", "New Passenger");
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", 1);
        result.put("name", "New Passenger");

        when(adminService.createPassenger(any())).thenReturn(result);

        mockMvc.perform(post("/admin/passengers")
                        .requestAttr("role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testUpdateSettingsSuccess() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("openTime", "08:00");
        
        when(adminService.updateSettings(any())).thenReturn(payload);

        mockMvc.perform(put("/admin/settings")
                        .requestAttr("role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openTime").value("08:00"));
    }

    @Test
    void testBlockVotingSuccess() throws Exception {
        Map<String, Boolean> payload = new HashMap<>();
        payload.put("block", true);
        
        Map<String, Object> result = new HashMap<>();
        result.put("blockedNextDay", true);

        when(adminService.blockVoting(true)).thenReturn(result);

        mockMvc.perform(post("/admin/settings/block-voting")
                        .requestAttr("role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.blockedNextDay").value(true));
    }

    @Test
    void testReplaceVehicleForbidden() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("newBusId", 2);

        mockMvc.perform(put("/admin/routes/1/replace-vehicle")
                        .requestAttr("role", "USER")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isForbidden());
    }
    @Test
    void testRejectSuccess() throws Exception {
        doNothing().when(adminService).reject(1L);

        mockMvc.perform(post("/admin/reject/1")
                        .requestAttr("role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testGetPassengersSuccess() throws Exception {
        when(adminService.getPassengers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/admin/passengers")
                        .requestAttr("role", "ADMIN"))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdatePassengerSuccess() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("name", "Updated");

        when(adminService.updatePassenger(anyLong(), any())).thenReturn(payload);

        mockMvc.perform(put("/admin/passengers/1")
                        .requestAttr("role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk());
    }

    @Test
    void testDeletePassengerSuccess() throws Exception {
        when(adminService.deletePassenger(anyLong())).thenReturn(new HashMap<>());

        mockMvc.perform(delete("/admin/passengers/1")
                        .requestAttr("role", "ADMIN"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetRoutesSuccess() throws Exception {
        when(adminService.getRoutes()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/admin/routes")
                        .requestAttr("role", "ADMIN"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetVehiclesSuccess() throws Exception {
        when(adminService.getVehicles()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/admin/vehicles")
                        .requestAttr("role", "ADMIN"))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateVehicleSuccess() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("plate", "ABC-1234");

        when(adminService.createVehicle(any())).thenReturn(payload);

        mockMvc.perform(post("/admin/vehicles")
                        .requestAttr("role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated());
    }

    @Test
    void testGetDriversSuccess() throws Exception {
        when(adminService.getDrivers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/admin/drivers")
                        .requestAttr("role", "ADMIN"))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateDriverSuccess() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("name", "Driver");

        when(adminService.createDriver(any())).thenReturn(payload);

        mockMvc.perform(post("/admin/drivers")
                        .requestAttr("role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated());
    }

    @Test
    void testGetSettingsSuccess() throws Exception {
        when(adminService.getSettings()).thenReturn(new HashMap<>());

        mockMvc.perform(get("/admin/settings")
                        .requestAttr("role", "ADMIN"))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateNoticeSuccess() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("title", "Notice");

        when(adminService.createNotice(any())).thenReturn(payload);

        mockMvc.perform(post("/admin/notices")
                        .requestAttr("role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated());
    }

    @Test
    void testGetSupportMessagesSuccess() throws Exception {
        when(adminService.getSupportMessages()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/admin/support-messages")
                        .requestAttr("role", "ADMIN"))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateRouteSuccess() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("name", "Route");

        when(adminService.createRoute(any())).thenReturn(payload);

        mockMvc.perform(post("/admin/routes")
                        .requestAttr("role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated());
    }

    @Test
    void testGetPassengerReportsSuccess() throws Exception {
        when(adminService.getPassengerReports()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/admin/reports/passengers")
                        .requestAttr("role", "ADMIN"))
                .andExpect(status().isOk());
    }

    @Test
    void testReplaceVehicleSuccess() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("newBusId", 2);

        when(adminService.replaceVehicle(anyLong(), any())).thenReturn(payload);

        mockMvc.perform(put("/admin/routes/1/replace-vehicle")
                        .requestAttr("role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk());
    }
}
