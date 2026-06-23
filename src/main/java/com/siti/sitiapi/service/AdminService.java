package com.siti.sitiapi.service;

import com.siti.sitiapi.repository.AdminRepository;
import com.siti.sitiapi.repository.UserRepository;
import com.siti.sitiapi.repository.PassengerRepository;
import com.siti.sitiapi.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final PassengerRepository passengerRepository;

    public List<Map<String, Object>> getPendingHomologations() {
        return adminRepository.getPendingHomologations();
    }

    @Transactional
    public void homologate(Long id) {
        adminRepository.approveHomologation(id);
        
        // Garante que o registro de passageiro correspondente existe
        if (!passengerRepository.existsById(id)) {
            passengerRepository.create(
                    id,
                    LocalDate.of(1998, 5, 15),
                    "(88) 99999-9999",
                    "Estudante",
                    "20260042",
                    "comprovante_matricula_2026.pdf",
                    null
            );
        }
    }

    @Transactional
    public void reject(Long id) {
        adminRepository.rejectHomologation(id);
    }

    public List<Map<String, Object>> getRoutes() {
        return adminRepository.getRoutes();
    }

    public List<Map<String, Object>> getVehicles() {
        return adminRepository.getVehicles();
    }

    public Map<String, Object> createVehicle(Map<String, Object> payload) {
        String plate = (String) payload.get("plate");
        String model = (String) payload.get("model");
        String year = String.valueOf(payload.get("year"));
        int capacity = ((Number) payload.get("capacity")).intValue();
        
        String accStr = (String) payload.get("accessibility");
        boolean accessibility = "Sim".equalsIgnoreCase(accStr) || "Sim (Elevador)".equalsIgnoreCase(accStr);

        Long id = adminRepository.insertVehicle(plate, model, year, capacity, accessibility);
        
        return Map.of(
                "id", id,
                "plate", plate,
                "model", model,
                "year", year,
                "capacity", capacity,
                "accessibility", accStr != null ? accStr : "Sim",
                "status", "Ativo",
                "votes", 0
        );
    }

    public List<Map<String, Object>> getDrivers() {
        return adminRepository.getDrivers();
    }

    @Transactional
    public Map<String, Object> createDriver(Map<String, Object> payload) {
        String name = (String) payload.get("name");
        String cnh = (String) payload.get("cnh");
        String category = (String) payload.get("category");
        String birthDateStr = (String) payload.get("birthDate");
        String validityStr = (String) payload.get("validity");
        String phone = (String) payload.get("phone");
        String email = (String) payload.get("email");

        LocalDate birthDate = LocalDate.parse(birthDateStr, DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate validity = LocalDate.parse(validityStr, DateTimeFormatter.ISO_LOCAL_DATE);

        // 1. Cadastra o usuário
        userRepository.create(email, "123456", cnh, name);
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("Erro ao criar usuário para o motorista.");
        }

        // Ativa o usuário
        adminRepository.approveHomologation(user.getId());

        // 2. Cadastra o motorista no banco de dados
        adminRepository.insertDriver(
                user.getId(), name, phone, java.sql.Date.valueOf(birthDate), cnh, category, java.sql.Date.valueOf(validity)
        );

        return Map.of(
                "id", user.getId(),
                "name", name,
                "cnh", cnh,
                "category", category,
                "birthDate", birthDateStr,
                "validity", validityStr,
                "phone", phone,
                "email", email,
                "status", "Ativo"
        );
    }

    public Map<String, Object> getSettings() {
        List<Map<String, Object>> settings = adminRepository.getSettings();
        if (settings.isEmpty()) {
            return Map.of(
                    "openTime", "06:00",
                    "closeTime", "17:00",
                    "blockedNextDay", false
            );
        }
        return settings.get(0);
    }

    public Map<String, Object> updateSettings(Map<String, Object> payload) {
        String openTime = (String) payload.get("openTime");
        String closeTime = (String) payload.get("closeTime");
        boolean blockedNextDay = (Boolean) payload.get("blockedNextDay");

        adminRepository.updateSettings(openTime, closeTime, blockedNextDay);
        return Map.of(
                "openTime", openTime,
                "closeTime", closeTime,
                "blockedNextDay", blockedNextDay
        );
    }

    public Map<String, Object> createNotice(Map<String, Object> payload) {
        String title = (String) payload.get("title");
        String message = (String) payload.get("message");

        Long id = adminRepository.insertNotice(title, message);
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        return Map.of(
                "id", id,
                "date", today,
                "title", title,
                "message", message
        );
    }

    public List<Map<String, Object>> getSupportMessages() {
        return adminRepository.getSupportMessages();
    }
}
