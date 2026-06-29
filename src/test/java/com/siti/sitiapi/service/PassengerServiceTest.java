package com.siti.sitiapi.service;

import com.siti.sitiapi.dto.PassengerCreateRequest;
import com.siti.sitiapi.dto.PassengerResponse;
import com.siti.sitiapi.exception.BusinessException;
import com.siti.sitiapi.model.Passenger;
import com.siti.sitiapi.model.User;
import com.siti.sitiapi.repository.PassengerRepository;
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
public class PassengerServiceTest {

    @Mock
    private PassengerRepository passengerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JdbcTemplate jdbc;
    
    @Mock
    private EmailService emailService;

    @InjectMocks
    private PassengerService passengerService;

    private Faker faker;

    @BeforeEach
    void setUp() {
        faker = new Faker(new Locale("pt", "BR"));
    }

    @Test
    void testCreateUserNotFound() {
        PassengerCreateRequest request = new PassengerCreateRequest();
        request.setIdUser(1L);

        when(userRepository.findById(1L)).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class, () -> passengerService.create(request));
        assertEquals(400, exception.getError().getStatus());
        assertEquals("Usuário não encontrado para o ID informado.", exception.getError().getMessage());
    }

    @Test
    void testCreateAlreadyExists() {
        PassengerCreateRequest request = new PassengerCreateRequest();
        request.setIdUser(1L);

        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(user);
        when(passengerRepository.existsById(1L)).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class, () -> passengerService.create(request));
        assertEquals(400, exception.getError().getStatus());
        assertEquals("Passageiro já cadastrado para este usuário.", exception.getError().getMessage());
    }

    @Test
    void testCreateSuccess() {
        PassengerCreateRequest request = new PassengerCreateRequest();
        request.setIdUser(1L);
        request.setRegistrationNumber("12345");

        User user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        when(userRepository.findById(1L)).thenReturn(user);
        when(passengerRepository.existsById(1L)).thenReturn(false);

        Passenger passenger = new Passenger();
        passenger.setId(1L);
        passenger.setRegistrationNumber("12345");
        when(passengerRepository.findById(1L)).thenReturn(passenger);

        PassengerResponse response = passengerService.create(request);
        assertEquals(1L, response.getId());
        assertEquals("12345", response.getRegistrationNumber());
        assertEquals("test@test.com", response.getEmail());
    }

    @Test
    void testGetRoutes() {
        List<Map<String, Object>> routes = new ArrayList<>();
        Map<String, Object> route = new HashMap<>();
        route.put("id", "1");
        route.put("code", "R01");
        route.put("name", "Rota 1");
        routes.add(route);

        when(jdbc.query(anyString(), any(RowMapper.class))).thenReturn(routes);
        when(jdbc.query(anyString(), any(RowMapper.class), eq(1L))).thenReturn(new ArrayList<>());
        
        List<Map<String, Object>> result = passengerService.getRoutes();
        assertEquals(1, result.size());
        assertEquals("1", result.get(0).get("id"));
    }

    @Test
    void testGetProfileUserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> passengerService.getProfile("test@test.com"));
        assertEquals("Usuário não encontrado.", exception.getMessage());
    }

    @Test
    void testGetProfilePassengerNotFoundCreatesDefault() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        when(userRepository.findByEmail("test@test.com")).thenReturn(user);
        
        // Return null first to trigger creation, then return a mocked passenger
        Passenger passenger = new Passenger();
        passenger.setRegistrationNumber("default_reg");
        when(passengerRepository.findById(1L)).thenReturn(null).thenReturn(passenger);
        
        Map<String, Object> result = passengerService.getProfile("test@test.com");
        assertEquals("test", result.get("name"));
        assertEquals("default_reg", result.get("registration"));
        verify(passengerRepository, times(1)).create(eq(1L), any(), any(), any(), any(), any(), any());
    }

    @Test
    void testVoteUserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> passengerService.vote("test@test.com", new HashMap<>()));
        assertEquals("Usuário não encontrado.", exception.getMessage());
    }

    @Test
    void testVoteSuccess() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findByEmail("test@test.com")).thenReturn(user);
        
        Map<String, Object> payload = new HashMap<>();
        payload.put("routeId", 1);
        payload.put("stop", "Parada 2");

        Map<String, Object> result = passengerService.vote("test@test.com", payload);
        assertEquals(true, result.get("success"));
        assertEquals("1", result.get("routeId"));
        assertEquals("Parada 2", result.get("stop"));
        verify(jdbc, times(1)).update(anyString(), eq(1L));
        verify(jdbc, times(1)).update(anyString(), eq(1L), eq(1L), eq("Parada 2"));
    }

    @Test
    void testGetNotices() {
        when(jdbc.query(anyString(), any(RowMapper.class))).thenReturn(new ArrayList<>());
        assertNotNull(passengerService.getNotices());
    }

    @Test
    void testGetContactsUserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> passengerService.getContacts("test@test.com"));
        assertEquals("Usuário não encontrado.", exception.getMessage());
    }

    @Test
    void testGetContactsDefault() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findByEmail("test@test.com")).thenReturn(user);
        when(jdbc.query(anyString(), any(RowMapper.class), eq(1L))).thenReturn(new ArrayList<>());

        Map<String, Object> result = passengerService.getContacts("test@test.com");
        Map<String, Object> driver = (Map<String, Object>) result.get("driver");
        assertEquals("Carlos Silva (Motorista)", driver.get("name"));
    }

    @Test
    void testGetContactsRealDriver() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findByEmail("test@test.com")).thenReturn(user);
        when(jdbc.query(anyString(), any(RowMapper.class), eq(1L))).thenReturn(List.of(2L));
        
        Map<String, Object> realDriver = new HashMap<>();
        realDriver.put("name", "João (Motorista)");
        when(jdbc.query(anyString(), any(RowMapper.class), eq(2L))).thenReturn(List.of(realDriver));

        Map<String, Object> result = passengerService.getContacts("test@test.com");
        Map<String, Object> driver = (Map<String, Object>) result.get("driver");
        assertEquals("João (Motorista)", driver.get("name"));
    }

    @Test
    void testSubmitSupportUserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> passengerService.submitSupport("test@test.com", new HashMap<>()));
        assertEquals("Usuário não encontrado.", exception.getMessage());
    }

    @Test
    void testSubmitSupportSuccess() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findByEmail("test@test.com")).thenReturn(user);
        
        Map<String, Object> payload = new HashMap<>();
        payload.put("subject", "Problema");
        payload.put("message", "App travando");

        Map<String, Object> result = passengerService.submitSupport("test@test.com", payload);
        assertEquals(true, result.get("success"));
        verify(emailService, times(1)).sendSimpleMessage(anyString(), anyString(), anyString());
    }

    @Test
    void testUploadPhotoUserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> passengerService.uploadPhoto("test@test.com"));
        assertEquals("Usuário não encontrado.", exception.getMessage());
    }

    @Test
    void testUploadPhotoSuccess() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findByEmail("test@test.com")).thenReturn(user);
        
        Map<String, Object> result = passengerService.uploadPhoto("test@test.com");
        assertEquals(true, result.get("success"));
        assertTrue(((String)result.get("photoUrl")).contains("1.jpg"));
        verify(passengerRepository, times(1)).updatePhotoUrl(eq(1L), anyString());
    }
}
