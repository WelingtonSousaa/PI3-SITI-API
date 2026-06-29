package com.siti.sitiapi.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RouteOptimizationServiceTest {

    @Mock
    private JdbcTemplate jdbc;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private RouteOptimizationService routeOptimizationService;

    @Test
    void testOptimizeStopsForTodaySuccess() {
        // Arrange
        when(jdbc.queryForList(anyString(), eq(Long.class))).thenReturn(List.of(1L, 2L));
        
        List<Map<String, Object>> mockStops = new ArrayList<>();
        Map<String, Object> stop = new HashMap<>();
        stop.put("stop_name", "Parada 1");
        stop.put("total", 5);
        mockStops.add(stop);
        
        when(jdbc.queryForList(anyString(), eq(1L))).thenReturn(mockStops);
        when(jdbc.queryForList(anyString(), eq(2L))).thenReturn(new ArrayList<>());

        // Act
        routeOptimizationService.optimizeStopsForToday();

        // Assert
        verify(jdbc, times(1)).queryForList(anyString(), eq(Long.class));
        verify(jdbc, times(1)).queryForList(anyString(), eq(1L));
        verify(jdbc, times(1)).queryForList(anyString(), eq(2L));
    }

    @Test
    void testAnalyzeCapacityAndDemandExceeded() {
        // Arrange
        List<Map<String, Object>> mockTrips = new ArrayList<>();
        Map<String, Object> trip = new HashMap<>();
        trip.put("id", 1L);
        trip.put("capacity", 40);
        trip.put("route_name", "Rota 1");
        mockTrips.add(trip);

        when(jdbc.queryForList(anyString())).thenReturn(mockTrips);
        when(jdbc.queryForObject(anyString(), eq(Integer.class), eq(1L))).thenReturn(45); // 45 votes > 40 capacity

        // Act
        routeOptimizationService.analyzeCapacityAndDemand();

        // Assert
        verify(emailService, times(1)).sendSimpleMessage(
                eq("admin@siti.edu.br"), 
                eq("ALERTA CRÍTICO: Lotação Excedida"), 
                anyString()
        );
    }

    @Test
    void testAnalyzeCapacityAndDemandLowDemand() {
        // Arrange
        List<Map<String, Object>> mockTrips = new ArrayList<>();
        Map<String, Object> trip = new HashMap<>();
        trip.put("id", 1L);
        trip.put("capacity", 40);
        trip.put("route_name", "Rota 1");
        mockTrips.add(trip);

        when(jdbc.queryForList(anyString())).thenReturn(mockTrips);
        when(jdbc.queryForObject(anyString(), eq(Integer.class), eq(1L))).thenReturn(10); // 10 / 40 = 25% (< 30%)

        // Act
        routeOptimizationService.analyzeCapacityAndDemand();

        // Assert
        verify(emailService, times(1)).sendSimpleMessage(
                eq("admin@siti.edu.br"), 
                eq("Sugestão de Otimização (RF017)"), 
                anyString()
        );
    }

    @Test
    void testAnalyzeCapacityAndDemandNormal() {
        // Arrange
        List<Map<String, Object>> mockTrips = new ArrayList<>();
        Map<String, Object> trip = new HashMap<>();
        trip.put("id", 1L);
        trip.put("capacity", 40);
        trip.put("route_name", "Rota 1");
        mockTrips.add(trip);

        when(jdbc.queryForList(anyString())).thenReturn(mockTrips);
        when(jdbc.queryForObject(anyString(), eq(Integer.class), eq(1L))).thenReturn(25); // 25 / 40 = 62.5% (normal)

        // Act
        routeOptimizationService.analyzeCapacityAndDemand();

        // Assert
        verify(emailService, never()).sendSimpleMessage(anyString(), anyString(), anyString());
    }

    @Test
    void testAnalyzeCapacityAndDemandZeroCapacity() {
        // Arrange
        List<Map<String, Object>> mockTrips = new ArrayList<>();
        Map<String, Object> trip = new HashMap<>();
        trip.put("id", 1L);
        trip.put("capacity", 0);
        trip.put("route_name", "Rota 1");
        mockTrips.add(trip);

        when(jdbc.queryForList(anyString())).thenReturn(mockTrips);
        when(jdbc.queryForObject(anyString(), eq(Integer.class), eq(1L))).thenReturn(0); 

        // Act
        routeOptimizationService.analyzeCapacityAndDemand();

        // Assert
        verify(emailService, never()).sendSimpleMessage(anyString(), anyString(), anyString());
    }
}
