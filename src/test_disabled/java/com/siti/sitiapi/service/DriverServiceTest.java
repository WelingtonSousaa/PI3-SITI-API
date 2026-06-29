package com.siti.sitiapi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DriverServiceTest {

    @Mock
    private JdbcTemplate jdbc;

    @InjectMocks
    private DriverService driverService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpdateTripStatus() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("status", "Em Andamento");

        Map<String, Object> response = driverService.updateTripStatus(12L, payload);

        assertEquals(true, response.get("success"));
        assertEquals("Em Andamento", response.get("status"));
        verify(jdbc, times(1)).update(contains("UPDATE trips SET status"), eq("Em Andamento"), eq(12L));
    }

    @Test
    void testReportFailure() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("vehiclePlate", "ABC-1234");
        payload.put("issueType", "Motor");
        payload.put("severity", "Alta");
        payload.put("description", "Vazamento de óleo");

        // Mock KeyHolder logic
        when(jdbc.update(any(PreparedStatementCreator.class), any(KeyHolder.class))).thenAnswer(invocation -> {
            KeyHolder keyHolder = invocation.getArgument(1);
            keyHolder.getKeyList().add(Map.of("GENERATED_KEY", 99L));
            return 1;
        });

        Map<String, Object> response = driverService.reportFailure(payload);

        assertEquals("ABC-1234", response.get("vehiclePlate"));
        assertEquals("Motor", response.get("issueType"));
        assertEquals("Registrado", response.get("status"));
    }
}
