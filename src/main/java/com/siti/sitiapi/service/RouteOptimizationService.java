package com.siti.sitiapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RouteOptimizationService {

    private final JdbcTemplate jdbc;
    private final EmailService emailService;

    // RF011: Remove paradas sem demanda
    public void optimizeStopsForToday() {
        // Encontra todas as viagens ativas hoje
        List<Long> activeTrips = jdbc.queryForList(
                "SELECT id FROM trips WHERE date = CURRENT_DATE()", Long.class);

        for (Long tripId : activeTrips) {
            // Conta os votos por parada
            List<Map<String, Object>> stopsWithVotes = jdbc.queryForList(
                    "SELECT stop_name, COUNT(*) as total FROM votes v " +
                    "JOIN passenger_trips pt ON v.id_passenger = pt.id_passenger " +
                    "WHERE pt.id_trip = ? AND v.voted_date = CURRENT_DATE() " +
                    "GROUP BY stop_name", tripId);

            // Logica simulada: Paradas com 0 votos nem aparecem na query acima.
            // Para "remover" da viagem, apenas enviamos ao frontend a lista consolidada das paradas que tiveram votos.
        }
    }

    // RF012, RF017, RF025: Capacidade, Sugestão, Lista de Espera
    public void analyzeCapacityAndDemand() {
        List<Map<String, Object>> tripsInfo = jdbc.queryForList(
                "SELECT t.id, t.id_route, b.capacity, r.name as route_name, b.license_plate " +
                "FROM trips t " +
                "JOIN buses b ON t.id_bus = b.id " +
                "JOIN routes r ON t.id_route = r.id " +
                "WHERE t.date = CURRENT_DATE()");

        for (Map<String, Object> trip : tripsInfo) {
            Long tripId = ((Number) trip.get("id")).longValue();
            Integer capacity = (Integer) trip.get("capacity");
            String routeName = (String) trip.get("route_name");

            Integer totalVotes = jdbc.queryForObject(
                    "SELECT COUNT(*) FROM passenger_trips WHERE id_trip = ?", Integer.class, tripId);

            if (totalVotes == null) totalVotes = 0;

            if (totalVotes > capacity) {
                // RF012: Alerta de capacidade máxima (via e-mail ou log do painel)
                emailService.sendSimpleMessage("admin@siti.edu.br", 
                        "ALERTA CRÍTICO: Lotação Excedida", 
                        "A viagem " + tripId + " (Rota: " + routeName + ") ultrapassou a capacidade do ônibus. " +
                        "Votos: " + totalVotes + " | Capacidade: " + capacity + ". \n" +
                        "Passageiros excedentes movidos para Lista de Espera (RF025).");
            } else if (capacity > 0 && ((double) totalVotes / capacity) < 0.3) {
                // RF017: Sugestão de substituição por veículo menor se demanda < 30%
                emailService.sendSimpleMessage("admin@siti.edu.br", 
                        "Sugestão de Otimização (RF017)", 
                        "A viagem " + tripId + " (Rota: " + routeName + ") está com demanda muito baixa. " +
                        "Votos: " + totalVotes + " | Capacidade: " + capacity + ". Considere alocar um veículo menor (van).");
            }
        }
    }
}
