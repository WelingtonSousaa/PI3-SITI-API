package com.siti.sitiapi.service;

import com.siti.sitiapi.repository.AdminRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AdminServiceTest {

    @Mock
    private AdminRepository adminRepository;
    
    @Mock
    private com.siti.sitiapi.repository.PassengerRepository passengerRepository;

    @InjectMocks
    private AdminService adminService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testApproveHomologation() {
        adminService.homologate(100L);
        verify(adminRepository, times(1)).approveHomologation(100L);
    }

    @Test
    void testCreateRoute() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("code", "R-TEST");
        payload.put("name", "Rota Teste");
        payload.put("description", "Desc");
        
        List<Map<String, Object>> stops = List.of(
            Map.of("street", "Rua A", "time", "08:00")
        );
        payload.put("stops", stops);

        when(adminRepository.insertRoute("R-TEST", "Rota Teste", "Desc")).thenReturn(10L);
        when(adminRepository.insertAddress("Rua A")).thenReturn(20L);
        when(adminRepository.insertSchedule("08:00")).thenReturn(30L);

        Map<String, Object> response = adminService.createRoute(payload);

        assertTrue((Boolean) response.get("success"));
        assertEquals(10L, response.get("id"));
        verify(adminRepository, times(1)).insertStop(10L, 20L, 30L);
    }

    @Test
    void testReplaceVehicle() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("newBusId", 50L);

        Map<String, Object> response = adminService.replaceVehicle(5L, payload);

        assertTrue((Boolean) response.get("success"));
        verify(adminRepository, times(1)).updateTripVehicle(5L, 50L);
    }
}
