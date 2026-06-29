package com.siti.sitiapi.service;

import com.siti.sitiapi.exception.BusinessException;
import com.siti.sitiapi.model.User;
import com.siti.sitiapi.repository.AdminRepository;
import com.siti.sitiapi.repository.PassengerRepository;
import com.siti.sitiapi.repository.UserRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PassengerRepository passengerRepository;

    @InjectMocks
    private AdminService adminService;

    private Faker faker;

    @BeforeEach
    void setUp() {
        faker = new Faker(new Locale("pt", "BR"));
    }

    @Test
    void testGetPendingHomologations() {
        when(adminRepository.getPendingHomologations()).thenReturn(new ArrayList<>());
        List<Map<String, Object>> result = adminService.getPendingHomologations();
        assertNotNull(result);
        verify(adminRepository, times(1)).getPendingHomologations();
    }

    @Test
    void testHomologatePassengerExists() {
        when(passengerRepository.existsById(1L)).thenReturn(true);
        adminService.homologate(1L);
        verify(adminRepository, times(1)).approveHomologation(1L);
        verify(passengerRepository, never()).create(any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void testHomologatePassengerDoesNotExist() {
        when(passengerRepository.existsById(1L)).thenReturn(false);
        adminService.homologate(1L);
        verify(adminRepository, times(1)).approveHomologation(1L);
        verify(passengerRepository, times(1)).create(any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void testRejectHomologation() {
        adminService.reject(1L);
        verify(adminRepository, times(1)).rejectHomologation(1L);
    }

    @Test
    void testGetPassengers() {
        when(adminRepository.getAllPassengers()).thenReturn(new ArrayList<>());
        List<Map<String, Object>> result = adminService.getPassengers();
        assertNotNull(result);
    }

    @Test
    void testCreatePassenger() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("name", "Test Name");
        payload.put("email", "test@test.com");
        payload.put("document", "12345678900");
        payload.put("phone", "11999999999");
        payload.put("type", "Estudante");
        payload.put("registrationCode", "REG123");

        User user = new User();
        user.setId(1L);
        when(userRepository.findByEmail("test@test.com")).thenReturn(user);

        Map<String, Object> result = adminService.createPassenger(payload);

        assertEquals(1L, result.get("id"));
        assertEquals("test@test.com", result.get("email"));
        verify(userRepository, times(1)).create(anyString(), anyString(), anyString(), anyString());
        verify(adminRepository, times(1)).approveHomologation(1L);
        verify(passengerRepository, times(1)).create(any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void testUpdatePassenger() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("name", "New Name");
        
        Map<String, Object> result = adminService.updatePassenger(1L, payload);
        assertEquals(true, result.get("success"));
        verify(adminRepository, times(1)).updatePassenger(eq(1L), any(), any(), any(), any());
    }

    @Test
    void testDeletePassenger() {
        Map<String, Object> result = adminService.deletePassenger(1L);
        assertEquals(true, result.get("success"));
        verify(adminRepository, times(1)).deletePassenger(1L);
    }

    @Test
    void testGetRoutes() {
        when(adminRepository.getRoutes()).thenReturn(new ArrayList<>());
        assertNotNull(adminService.getRoutes());
    }

    @Test
    void testGetVehicles() {
        when(adminRepository.getVehicles()).thenReturn(new ArrayList<>());
        assertNotNull(adminService.getVehicles());
    }

    @Test
    void testCreateVehicle() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("plate", "ABC-1234");
        payload.put("model", "Mercedes");
        payload.put("year", 2020);
        payload.put("capacity", 40);
        payload.put("accessibility", "Sim");

        when(adminRepository.insertVehicle(anyString(), anyString(), anyString(), anyInt(), anyBoolean())).thenReturn(1L);

        Map<String, Object> result = adminService.createVehicle(payload);
        assertEquals(1L, result.get("id"));
        assertEquals("Ativo", result.get("status"));
    }

    @Test
    void testGetDrivers() {
        when(adminRepository.getDrivers()).thenReturn(new ArrayList<>());
        assertNotNull(adminService.getDrivers());
    }

    @Test
    void testCreateDriverUserError() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("email", "driver@test.com");
        payload.put("birthDate", "1990-01-01");
        payload.put("validity", "2030-01-01");

        when(userRepository.findByEmail(anyString())).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> adminService.createDriver(payload));
        assertEquals("Erro ao criar usuário para o motorista.", exception.getMessage());
    }

    @Test
    void testCreateDriverSuccess() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("name", "Driver Name");
        payload.put("email", "driver@test.com");
        payload.put("birthDate", "1990-01-01");
        payload.put("validity", "2030-01-01");
        payload.put("cnh", "12345678900");
        payload.put("category", "D");
        payload.put("phone", "11999999999");

        User user = new User();
        user.setId(1L);
        when(userRepository.findByEmail(anyString())).thenReturn(user);

        Map<String, Object> result = adminService.createDriver(payload);
        assertEquals(1L, result.get("id"));
        assertEquals("Ativo", result.get("status"));
        verify(adminRepository, times(1)).insertDriver(any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void testGetSettingsEmpty() {
        when(adminRepository.getSettings()).thenReturn(new ArrayList<>());
        Map<String, Object> result = adminService.getSettings();
        assertEquals("06:00", result.get("openTime"));
        assertEquals("17:00", result.get("closeTime"));
        assertEquals(false, result.get("blockedNextDay"));
    }

    @Test
    void testGetSettingsPopulated() {
        List<Map<String, Object>> settingsList = new ArrayList<>();
        Map<String, Object> setting = new HashMap<>();
        setting.put("openTime", "07:00");
        settingsList.add(setting);

        when(adminRepository.getSettings()).thenReturn(settingsList);
        Map<String, Object> result = adminService.getSettings();
        assertEquals("07:00", result.get("openTime"));
    }

    @Test
    void testUpdateSettings() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("openTime", "08:00");
        payload.put("closeTime", "18:00");
        payload.put("blockedNextDay", true);

        Map<String, Object> result = adminService.updateSettings(payload);
        assertEquals("08:00", result.get("openTime"));
        verify(adminRepository, times(1)).updateSettings("08:00", "18:00", true);
    }

    @Test
    void testCreateNotice() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("title", "Aviso");
        payload.put("message", "Mensagem");

        when(adminRepository.insertNotice("Aviso", "Mensagem")).thenReturn(1L);
        Map<String, Object> result = adminService.createNotice(payload);
        assertEquals(1L, result.get("id"));
    }

    @Test
    void testGetSupportMessages() {
        when(adminRepository.getSupportMessages()).thenReturn(new ArrayList<>());
        assertNotNull(adminService.getSupportMessages());
    }

    @Test
    void testCreateRouteNoStops() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("code", "R01");
        
        when(adminRepository.insertRoute(any(), any(), any())).thenReturn(1L);

        Map<String, Object> result = adminService.createRoute(payload);
        assertEquals(1L, result.get("id"));
    }

    @Test
    void testCreateRouteWithStops() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("code", "R01");
        
        List<Map<String, Object>> stops = new ArrayList<>();
        Map<String, Object> stop = new HashMap<>();
        stop.put("street", "Rua A");
        stop.put("time", "12:00");
        stops.add(stop);
        payload.put("stops", stops);
        
        when(adminRepository.insertRoute(any(), any(), any())).thenReturn(1L);
        when(adminRepository.insertAddress(any())).thenReturn(2L);
        when(adminRepository.insertSchedule(any())).thenReturn(3L);

        Map<String, Object> result = adminService.createRoute(payload);
        assertEquals(1L, result.get("id"));
        verify(adminRepository, times(1)).insertStop(1L, 2L, 3L);
    }

    @Test
    void testBlockVoting() {
        Map<String, Object> result = adminService.blockVoting(true);
        assertEquals(true, result.get("blockedNextDay"));
        verify(adminRepository, times(1)).updateSettings("06:00", "17:00", true);
    }

    @Test
    void testGetPassengerReports() {
        when(adminRepository.getPassengerReports()).thenReturn(new ArrayList<>());
        assertNotNull(adminService.getPassengerReports());
    }

    @Test
    void testReplaceVehicle() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("newBusId", 2);
        
        Map<String, Object> result = adminService.replaceVehicle(1L, payload);
        assertEquals(true, result.get("success"));
        verify(adminRepository, times(1)).updateTripVehicle(1L, 2L);
    }
}
