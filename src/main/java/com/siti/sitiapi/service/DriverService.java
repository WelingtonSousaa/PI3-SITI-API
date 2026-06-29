package com.siti.sitiapi.service;

import com.siti.sitiapi.dto.DriverCreateRequest;
import com.siti.sitiapi.dto.DriverResponse;
import com.siti.sitiapi.exception.BusinessException;
import com.siti.sitiapi.dto.ErrorResponse;
import com.siti.sitiapi.model.Driver;
import com.siti.sitiapi.model.User;
import com.siti.sitiapi.repository.DriverRepository;
import com.siti.sitiapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverRepository repository;
    private final UserRepository userRepository;
    private final JdbcTemplate jdbc;

    public DriverResponse createDriver(DriverCreateRequest request) {
        User user = userRepository.findById(request.getIdUser());
        if (user == null) {
            throw new BusinessException(
                    new ErrorResponse(400, "Usuário não encontrado para o ID informado.", "/drivers/create")
            );
        }
        if (repository.existsById(request.getIdUser())) {
            throw new BusinessException(
                    new ErrorResponse(400, "Driver já cadastrado para este usuário.", "/drivers/create")
            );
        }

        repository.create(
            request.getIdUser(),
            request.getCnhNumber(),
            request.getCnhCategory(),
            request.getName(),
            request.getBirthDate(),
            request.getCnhValidityDate(),
            request.getPhone(),
            request.getIdAddress()
        );
        
        Driver driver = repository.findById(request.getIdUser());
        DriverResponse response = new DriverResponse();
        response.setId(driver.getId());
        response.setCnhNumber(driver.getCnhNumber());
        response.setCnhCategory(driver.getCnhCategory() != null ? driver.getCnhCategory().name() : null);
        response.setName(driver.getName());
        response.setBirthDate(driver.getBirthDate());
        response.setCnhValidityDate(driver.getCnhValidityDate());
        response.setPhone(driver.getPhone());
        response.setIdAddress(driver.getIdAddress());

        return response;
    }

    public List<Map<String, Object>> getRoutes(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("Motorista não encontrado.");
        }

        List<Map<String, Object>> trips = jdbc.query(
                "SELECT t.id, r.code, r.name, t.status, b.license_plate AS bus, " +
                "       (SELECT sch.time FROM stops s JOIN schedules sch ON s.id_schedule = sch.id WHERE s.id_route = r.id LIMIT 1) AS time " +
                "FROM trips t " +
                "JOIN routes r ON t.id_route = r.id " +
                "JOIN buses b ON t.id_bus = b.id " +
                "WHERE t.id_driver = ? AND t.date = CURRENT_DATE()",
                (rs, rowNum) -> {
                    Map<String, Object> map = new java.util.HashMap<>();
                    map.put("id", rs.getLong("id"));
                    map.put("code", rs.getString("code") != null ? rs.getString("code") : "");
                    map.put("name", rs.getString("name") != null ? rs.getString("name") : "");
                    map.put("time", rs.getString("time") != null ? rs.getString("time") : "08:30");
                    map.put("bus", rs.getString("bus") != null ? rs.getString("bus") : "");
                    map.put("status", rs.getString("status") != null ? rs.getString("status") : "Agendada");
                    return map;
                },
                user.getId()
        );

        return trips;
    }

    public Map<String, Object> getProfile(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("Motorista não encontrado.");
        }
        Driver driver = repository.findById(user.getId());
        if (driver == null) {
            throw new RuntimeException("Dados operacionais do motorista não encontrados.");
        }

        return Map.of(
                "id", user.getId(),
                "name", driver.getName() != null ? driver.getName() : "Carlos Motorista",
                "cnh", driver.getCnhNumber() != null ? driver.getCnhNumber() : "12345678901",
                "category", driver.getCnhCategory() != null ? driver.getCnhCategory().name() : "D",
                "birthDate", driver.getBirthDate() != null ? driver.getBirthDate().toString() : "1985-06-23",
                "validity", driver.getCnhValidityDate() != null ? driver.getCnhValidityDate().toString() : "2031-12-31",
                "phone", driver.getPhone() != null ? driver.getPhone() : "(88) 98888-7777",
                "email", email,
                "status", "Ativo"
        );
    }

    public Map<String, Object> getVehicle(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("Motorista não encontrado.");
        }

        List<Map<String, Object>> vehicles = jdbc.query(
                "SELECT b.bus_model AS model, b.license_plate AS plate, b.manufacturing_year, " +
                "       b.capacity, CASE WHEN b.accessibility = 1 THEN 'Sim (Elevador)' ELSE 'Não' END AS accessibility, " +
                "       COALESCE(b.operation_status, 'Excelente') AS status " +
                "FROM trips t " +
                "JOIN buses b ON t.id_bus = b.id " +
                "WHERE t.id_driver = ? AND t.date = CURRENT_DATE() LIMIT 1",
                (rs, rowNum) -> {
                    Map<String, Object> map = new java.util.HashMap<>();
                    map.put("model", rs.getString("model") != null ? rs.getString("model") : "");
                    map.put("plate", rs.getString("plate") != null ? rs.getString("plate") : "");
                    map.put("year", rs.getString("manufacturing_year") != null ? rs.getString("manufacturing_year") : "");
                    map.put("capacity", rs.getInt("capacity"));
                    map.put("accessibility", rs.getString("accessibility") != null ? rs.getString("accessibility") : "Não");
                    map.put("status", rs.getString("status") != null ? rs.getString("status") : "Excelente");
                    return map;
                },
                user.getId()
        );

        if (vehicles.isEmpty()) {
            return Map.of(); // Retorna vazio se não tiver ônibus escalado hoje
        }

        return vehicles.get(0);
    }

    public Map<String, Object> updateTripStatus(Long id, Map<String, Object> payload) {
        String status = (String) payload.get("status");
        jdbc.update("UPDATE trips SET status = ? WHERE id = ?", status, id);

        return Map.of(
                "success", true,
                "id", id,
                "status", status
        );
    }

    public List<Map<String, Object>> getPassengers(Long routeId) {
        List<Map<String, Object>> passengers = jdbc.query(
                "SELECT u.id, u.name, p.registration_number AS registration, v.stop_name AS stop, " +
                "       p.photo_url AS photo, v.status, " +
                "       CASE WHEN p.type = 'Necessita Acessibilidade' THEN 1 ELSE 0 END AS requiresAccessibility " +
                "FROM votes v " +
                "JOIN users u ON v.id_passenger = u.id " +
                "JOIN passengers p ON u.id = p.id " +
                "WHERE v.route_id = ? AND v.voted_date = CURRENT_DATE()",
                (rs, rowNum) -> {
                    boolean reqAcc = rs.getInt("requiresAccessibility") == 1;
                    Map<String, Object> map = new java.util.HashMap<>();
                    map.put("id", rs.getLong("id"));
                    map.put("name", rs.getString("name") != null ? rs.getString("name") : "");
                    map.put("registration", rs.getString("registration") != null ? rs.getString("registration") : "");
                    map.put("stop", rs.getString("stop") != null ? rs.getString("stop") : "");
                    map.put("requiresAccessibility", reqAcc);
                    map.put("accessibilityDetail", reqAcc ? "Cadeirante - Necessita de Elevador" : "");
                    map.put("photo", rs.getString("photo") != null ? rs.getString("photo") : "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150");
                    map.put("status", rs.getString("status") != null ? rs.getString("status") : "Pendente");
                    return map;
                },
                routeId
        );

        return passengers;
    }

    public Map<String, Object> updatePassengerStatus(Long passengerId, Map<String, Object> payload) {
        String status = (String) payload.get("status");
        
        // Atualiza o voto de hoje do passageiro para o status especificado
        jdbc.update("UPDATE votes SET status = ? WHERE id_passenger = ? AND voted_date = CURRENT_DATE()",
                status, passengerId);

        return Map.of(
                "success", true,
                "id", passengerId,
                "status", status
        );
    }

    public Map<String, Object> reportFailure(Map<String, Object> payload) {
        String plate = (String) payload.get("vehiclePlate");
        String issue = (String) payload.get("issueType");
        String severity = (String) payload.get("severity");
        String desc = (String) payload.get("description");

        org.springframework.jdbc.support.KeyHolder keyHolder = new org.springframework.jdbc.support.GeneratedKeyHolder();
        jdbc.update(connection -> {
            java.sql.PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO failures (vehicle_plate, issue_type, severity, description, status) VALUES (?, ?, ?, ?, 'Registrado')",
                    new String[]{"id"}
            );
            ps.setString(1, plate);
            ps.setString(2, issue);
            ps.setString(3, severity);
            ps.setString(4, desc);
            return ps;
        }, keyHolder);

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        return Map.of(
                "id", keyHolder.getKey() != null ? keyHolder.getKey().longValue() : 0L,
                "date", today,
                "vehiclePlate", plate,
                "issueType", issue,
                "severity", severity,
                "description", desc,
                "status", "Registrado"
        );
    }
}