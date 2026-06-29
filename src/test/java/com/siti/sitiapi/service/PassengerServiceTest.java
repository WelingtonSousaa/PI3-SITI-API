package com.siti.sitiapi.service;

import com.siti.sitiapi.model.User;
import com.siti.sitiapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class PassengerServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JdbcTemplate jdbc;

    @InjectMocks
    private PassengerService passengerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testVoteSuccess() {
        User user = new User();
        user.setId(77L);
        user.setEmail("student@siti.edu.br");

        when(userRepository.findByEmail("student@siti.edu.br")).thenReturn(user);

        Map<String, Object> payload = new HashMap<>();
        payload.put("routeId", 15);
        payload.put("stop", "Centro");

        Map<String, Object> response = passengerService.vote("student@siti.edu.br", payload);

        assertEquals(true, response.get("success"));
        verify(jdbc, times(1)).update(contains("DELETE FROM votes"), eq(77L));
        verify(jdbc, times(1)).update(contains("INSERT INTO votes"), eq(77L), eq(15L), eq("Centro"));
    }

    @Test
    void testVoteUserNotFound() {
        when(userRepository.findByEmail("notfound@siti.edu.br")).thenReturn(null);

        Map<String, Object> payload = new HashMap<>();
        
        Exception exception = assertThrows(RuntimeException.class, () -> passengerService.vote("notfound@siti.edu.br", payload));
        assertEquals("Usuário não encontrado.", exception.getMessage());
    }
}
