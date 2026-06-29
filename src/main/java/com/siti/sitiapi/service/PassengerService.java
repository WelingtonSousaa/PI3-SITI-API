package com.siti.sitiapi.service;

import com.siti.sitiapi.dto.ErrorResponse;
import com.siti.sitiapi.dto.PassengerCreateRequest;
import com.siti.sitiapi.dto.PassengerResponse;
import com.siti.sitiapi.exception.BusinessException;
import com.siti.sitiapi.model.Passenger;
import com.siti.sitiapi.model.User;
import com.siti.sitiapi.repository.PassengerRepository;
import com.siti.sitiapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PassengerService {

    private final PassengerRepository passengerRepository;
    private final UserRepository userRepository;
    private final JdbcTemplate jdbc;
    private final EmailService emailService;

    public PassengerResponse create(PassengerCreateRequest request) {
        User user = userRepository.findById(request.getIdUser());
        if (user == null) {
            throw new BusinessException(
                    new ErrorResponse(400, "Usuário não encontrado para o ID informado.", "/passengers/create")
            );
        }

        if (passengerRepository.existsById(request.getIdUser())) {
            throw new BusinessException(
                    new ErrorResponse(400, "Passageiro já cadastrado para este usuário.", "/passengers/create")
            );
        }

        passengerRepository.create(
                request.getIdUser(),
                request.getBirthDate(),
                request.getPhone(),
                request.getType(),
                request.getRegistrationNumber(),
                request.getBondProof(),
                request.getIdAddress()
        );

        Passenger passenger = passengerRepository.findById(request.getIdUser());

        PassengerResponse response = new PassengerResponse();
        response.setId(passenger.getId());
        response.setEmail(user.getEmail());
        response.setStatus(user.getStatus());
        response.setBirthDate(passenger.getBirthDate());
        response.setPhone(passenger.getPhone());
        response.setType(passenger.getType());
        response.setRegistrationNumber(passenger.getRegistrationNumber());
        response.setBondProof(passenger.getBondProof());
        response.setIdAddress(passenger.getIdAddress());
        return response;
    }

    public List<Map<String, Object>> getRoutes() {
        List<Map<String, Object>> routes = jdbc.query(
                "SELECT id, code, name FROM routes WHERE status = 'Ativa' OR status = 'Ativo'",
                (rs, rowNum) -> Map.of(
                        "id", String.valueOf(rs.getLong("id")),
                        "code", rs.getString("code") != null ? rs.getString("code") : "",
                        "name", rs.getString("name") != null ? rs.getString("name") : ""
                )
        );

        return routes.stream().map(route -> {
            String routeId = (String) route.get("id");
            List<String> stops = jdbc.query(
                    "SELECT a.street AS stop FROM stops s JOIN addresses a ON s.id_address = a.id WHERE s.id_route = ?",
                    (rs, rowNum) -> rs.getString("stop"),
                    Long.parseLong(routeId)
            );

            List<Boolean> accessList = jdbc.query(
                    "SELECT b.accessibility FROM trips t JOIN buses b ON t.id_bus = b.id WHERE t.id_route = ? AND t.date = CURRENT_DATE() LIMIT 1",
                    (rs, rowNum) -> rs.getBoolean("accessibility"),
                    Long.parseLong(routeId)
            );
            String accessibility = (!accessList.isEmpty() && accessList.get(0)) ? "Sim (Elevador)" : "Não";

            List<String> timeList = jdbc.query(
                    "SELECT sch.time FROM stops s JOIN schedules sch ON s.id_schedule = sch.id WHERE s.id_route = ? LIMIT 1",
                    (rs, rowNum) -> rs.getString("time"),
                    Long.parseLong(routeId)
            );
            String time = !timeList.isEmpty() ? timeList.get(0) : "18:00";

            return Map.of(
                    "id", routeId,
                    "code", route.get("code"),
                    "name", route.get("name"),
                    "stops", stops,
                    "accessibility", accessibility,
                    "time", time
            );
        }).collect(java.util.stream.Collectors.toList());
    }

    public Map<String, Object> getProfile(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("Usuário não encontrado.");
        }
        Passenger passenger = passengerRepository.findById(user.getId());
        if (passenger == null) {
            // Se o passageiro não existe, cria um padrão
            passengerRepository.create(
                    user.getId(),
                    LocalDate.of(1998, 5, 15),
                    "(88) 99999-9999",
                    "Campus Universitário",
                    "20260042",
                    "comprovante_matricula_2026.pdf",
                    null
            );
            passenger = passengerRepository.findById(user.getId());
        }

        String name = user.getName() != null ? user.getName() : AuthService.formatNameFromEmail(email);
        String reg = passenger.getRegistrationNumber() != null ? passenger.getRegistrationNumber() : "20260042";
        String inst = passenger.getType() != null ? passenger.getType() : "Campus Universitário";
        String photo = passenger.getPhotoUrl() != null ? passenger.getPhotoUrl() : "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150";

        return Map.of(
                "name", name,
                "registration", reg,
                "institution", inst,
                "status", user.getStatus() != null ? user.getStatus() : "Ativo",
                "photoUrl", photo
        );
    }

    public Map<String, Object> vote(String email, Map<String, Object> payload) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("Usuário não encontrado.");
        }
        String routeIdStr = String.valueOf(payload.get("routeId"));
        String stop = (String) payload.get("stop");

        // Remove voto anterior de hoje
        jdbc.update("DELETE FROM votes WHERE id_passenger = ? AND voted_date = CURRENT_DATE()", user.getId());

        // Insere o novo voto
        jdbc.update("INSERT INTO votes (id_passenger, route_id, stop_name, voted_date) VALUES (?, ?, ?, CURRENT_DATE())",
                user.getId(), Long.parseLong(routeIdStr), stop);

        return Map.of(
                "success", true,
                "routeId", routeIdStr,
                "stop", stop
        );
    }

    public List<Map<String, Object>> getNotices() {
        return jdbc.query(
                "SELECT id, DATE_FORMAT(created_at, '%d/%m/%Y') AS date, title, message FROM notices ORDER BY created_at DESC",
                (rs, rowNum) -> Map.of(
                        "id", rs.getLong("id"),
                        "date", rs.getString("date") != null ? rs.getString("date") : "",
                        "title", rs.getString("title") != null ? rs.getString("title") : "",
                        "message", rs.getString("message") != null ? rs.getString("message") : ""
                )
        );
    }

    public Map<String, Object> getContacts(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("Usuário não encontrado.");
        }

        // Tenta achar o voto do passageiro hoje
        List<Long> votedRoutes = jdbc.query(
                "SELECT route_id FROM votes WHERE id_passenger = ? AND voted_date = CURRENT_DATE()",
                (rs, rowNum) -> rs.getLong("route_id"),
                user.getId()
        );

        Map<String, Object> driverContact = Map.of(
                "name", "Carlos Silva (Motorista)",
                "phone", "(88) 98888-7777",
                "route", "Rota Universitária Centro"
        );

        if (!votedRoutes.isEmpty()) {
            Long rId = votedRoutes.get(0);
            List<Map<String, Object>> realDriver = jdbc.query(
                    "SELECT d.name, d.phone, r.name AS route " +
                    "FROM trips t " +
                    "JOIN drivers d ON t.id_driver = d.id " +
                    "JOIN routes r ON t.id_route = r.id " +
                    "WHERE t.id_route = ? AND t.date = CURRENT_DATE() LIMIT 1",
                    (rs, rowNum) -> Map.of(
                            "name", rs.getString("name") + " (Motorista)",
                            "phone", rs.getString("phone"),
                            "route", rs.getString("route")
                    ),
                    rId
            );
            if (!realDriver.isEmpty()) {
                driverContact = realDriver.get(0);
            }
        }

        return Map.of(
                "driver", driverContact,
                "admin", Map.of(
                        "name", "Setor de Transportes",
                        "phone", "(88) 3691-1234",
                        "email", "transportes@siti.edu.br"
                )
        );
    }

    public Map<String, Object> submitSupport(String email, Map<String, Object> payload) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("Usuário não encontrado.");
        }
        String subject = (String) payload.get("subject");
        String message = (String) payload.get("message");

        jdbc.update("INSERT INTO support_messages (user_id, subject, message) VALUES (?, ?, ?)",
                user.getId(), subject, message);

        emailService.sendSimpleMessage("transportes@siti.edu.br", 
                "Novo Suporte SITI: " + subject, 
                "Mensagem de: " + email + "\n\n" + message);

        return Map.of(
                "success", true,
                "subject", subject,
                "message", message
        );
    }

    public Map<String, Object> uploadPhoto(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("Usuário não encontrado.");
        }
        String photoUrl = "https://imagens-siti.s3.amazonaws.com/perfis/" + user.getId() + ".jpg";
        passengerRepository.updatePhotoUrl(user.getId(), photoUrl);

        return Map.of(
                "success", true,
                "photoUrl", photoUrl
        );
    }
}
