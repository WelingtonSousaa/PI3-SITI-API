package com.siti.sitiapi.service;

import com.siti.sitiapi.dto.DriverCreateRequest;
import com.siti.sitiapi.dto.DriverResponse;
import com.siti.sitiapi.exception.BusinessException;
import com.siti.sitiapi.model.Driver;
import com.siti.sitiapi.model.User;
import com.siti.sitiapi.repository.DriverRepository;
import com.siti.sitiapi.repository.UserRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DriverServiceTest {

    @Mock
    private DriverRepository repository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JdbcTemplate jdbc;

    @InjectMocks
    private DriverService driverService;

    private Faker faker;

    @BeforeEach
    void setUp() {
        faker = new Faker(new Locale("pt", "BR"));
    }

    @Test
    void testCreateDriverUserNotFound() {
        DriverCreateRequest request = new DriverCreateRequest();
        request.setIdUser(1L);

        when(userRepository.findById(1L)).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class, () -> driverService.createDriver(request));
        assertEquals(400, exception.getError().getStatus());
        assertEquals("Usuário não encontrado para o ID informado.", exception.getError().getMessage());
    }

    @Test
    void testCreateDriverAlreadyExists() {
        DriverCreateRequest request = new DriverCreateRequest();
        request.setIdUser(1L);

        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(user);
        when(repository.existsById(1L)).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class, () -> driverService.createDriver(request));
        assertEquals(400, exception.getError().getStatus());
        assertEquals("Driver já cadastrado para este usuário.", exception.getError().getMessage());
    }

    @Test
    void testCreateDriverSuccess() {
        DriverCreateRequest request = new DriverCreateRequest();
        request.setIdUser(1L);
        request.setCnhNumber(faker.cpf().valid());

        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(user);
        when(repository.existsById(1L)).thenReturn(false);

        Driver driver = new Driver();
        driver.setId(1L);
        driver.setCnhNumber(request.getCnhNumber());
        when(repository.findById(1L)).thenReturn(driver);

        DriverResponse response = driverService.createDriver(request);
        assertEquals(1L, response.getId());
        assertEquals(request.getCnhNumber(), response.getCnhNumber());
    }

    @Test
    void testGetRoutesUserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> driverService.getRoutes("test@test.com"));
        assertEquals("Motorista não encontrado.", exception.getMessage());
    }

    @Test
    void testGetRoutesSuccess() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findByEmail(anyString())).thenReturn(user);
        when(jdbc.query(anyString(), any(RowMapper.class), eq(1L))).thenReturn(new ArrayList<>());

        List<Map<String, Object>> result = driverService.getRoutes("test@test.com");
        assertNotNull(result);
    }

    @Test
    void testGetProfileUserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> driverService.getProfile("test@test.com"));
        assertEquals("Motorista não encontrado.", exception.getMessage());
    }

    @Test
    void testGetProfileDriverNotFound() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findByEmail(anyString())).thenReturn(user);
        when(repository.findById(1L)).thenReturn(null);
        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> driverService.getProfile("test@test.com"));
        assertEquals("Dados operacionais do motorista não encontrados.", exception.getMessage());
    }

    @Test
    void testGetProfileSuccess() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findByEmail(anyString())).thenReturn(user);
        
        Driver driver = new Driver();
        driver.setName("Test Driver");
        when(repository.findById(1L)).thenReturn(driver);
        
        Map<String, Object> result = driverService.getProfile("test@test.com");
        assertEquals("Test Driver", result.get("name"));
    }

    @Test
    void testGetVehicleUserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> driverService.getVehicle("test@test.com"));
        assertEquals("Motorista não encontrado.", exception.getMessage());
    }

    @Test
    void testGetVehicleEmpty() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findByEmail(anyString())).thenReturn(user);
        when(jdbc.query(anyString(), any(RowMapper.class), eq(1L))).thenReturn(new ArrayList<>());
        
        Map<String, Object> result = driverService.getVehicle("test@test.com");
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetVehicleSuccess() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findByEmail(anyString())).thenReturn(user);
        
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> vehicle = new HashMap<>();
        vehicle.put("plate", "ABC-1234");
        list.add(vehicle);
        
        when(jdbc.query(anyString(), any(RowMapper.class), eq(1L))).thenReturn(list);
        
        Map<String, Object> result = driverService.getVehicle("test@test.com");
        assertEquals("ABC-1234", result.get("plate"));
    }

    @Test
    void testUpdateTripStatus() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("status", "Concluído");
        
        Map<String, Object> result = driverService.updateTripStatus(1L, payload);
        assertEquals("Concluído", result.get("status"));
        verify(jdbc, times(1)).update(anyString(), eq("Concluído"), eq(1L));
    }

    @Test
    void testGetPassengers() {
        when(jdbc.query(anyString(), any(RowMapper.class), eq(1L))).thenReturn(new ArrayList<>());
        assertNotNull(driverService.getPassengers(1L));
    }

    @Test
    void testUpdatePassengerStatus() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("status", "Presente");
        
        Map<String, Object> result = driverService.updatePassengerStatus(1L, payload);
        assertEquals("Presente", result.get("status"));
        verify(jdbc, times(1)).update(anyString(), eq("Presente"), eq(1L));
    }

    @Test
    void testReportFailureSuccess() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("vehiclePlate", "ABC-1234");
        payload.put("issueType", "Pneu");
        payload.put("severity", "Alta");
        payload.put("description", "Pneu furado");

        // We can't easily verify the PreparedStatementCreator with generated keyholder directly 
        // without complex matchers, but we can test that it executes and returns the correct structure.
        when(jdbc.update(any(org.springframework.jdbc.core.PreparedStatementCreator.class), any(org.springframework.jdbc.support.KeyHolder.class))).thenAnswer(invocation -> {
            return 1;
        });

        Map<String, Object> result = driverService.reportFailure(payload);
        assertEquals("Registrado", result.get("status"));
        assertEquals("ABC-1234", result.get("vehiclePlate"));
    }
}
