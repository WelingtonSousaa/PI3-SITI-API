package com.siti.sitiapi.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SitiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Test
    public void testAdminEndpoints() throws Exception {
        // Pre-insert a pending user
        jdbcTemplate.update("INSERT INTO users (email, password, status, identifier_document, name) VALUES ('mariana.costa@crateus.edu.br', '123456', 'Pendente', '123.456.789-00', 'Mariana Costa de Melo')");

        // Verify Pending Homologations
        mockMvc.perform(get("/admin/pending-homologations")
                        .header("Authorization", "Bearer mock-jwt-token-admin@siti.com")
                        .header("Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.name == 'Mariana Costa de Melo')]").exists());

        // Approve student
        Long studentId = jdbcTemplate.queryForObject("SELECT id FROM users WHERE email = 'mariana.costa@crateus.edu.br'", Long.class);
        mockMvc.perform(post("/admin/homologate/" + studentId)
                        .header("Authorization", "Bearer mock-jwt-token-admin@siti.com")
                        .header("Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.id", is(studentId.intValue())));

        // Verify passenger is registered and status is Ativo
        String status = jdbcTemplate.queryForObject("SELECT status FROM users WHERE id = ?", String.class, studentId);
        org.junit.jupiter.api.Assertions.assertEquals("Ativo", status);
    }

    @Test
    public void testPassengerEndpoints() throws Exception {
        // Pre-insert active route and stops
        jdbcTemplate.update("INSERT INTO routes (code, name, status) VALUES ('R-CENTRO', 'Rota Universitária Centro', 'Ativa')");
        Long routeId = jdbcTemplate.queryForObject("SELECT id FROM routes WHERE code = 'R-CENTRO'", Long.class);
        jdbcTemplate.update("INSERT INTO addresses (id, neighborhood, street) VALUES (10, 'Centro', 'Praça Matriz')");
        jdbcTemplate.update("INSERT INTO schedules (id, time) VALUES (10, '18:00')");
        jdbcTemplate.update("INSERT INTO stops (id_route, id_address, id_schedule, status) VALUES (?, 10, 10, 'Ativo')", routeId);

        // Access passenger profile (will auto-provision mariana)
        mockMvc.perform(get("/passenger/profile")
                        .header("Authorization", "Bearer mock-jwt-token-mariana@siti.com")
                        .header("Role", "USER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Mariana Costa de Melo")))
                .andExpect(jsonPath("$.status", is("Ativo")));

        // Access passenger routes
        mockMvc.perform(get("/passenger/routes")
                        .header("Authorization", "Bearer mock-jwt-token-mariana@siti.com")
                        .header("Role", "USER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.code == 'R-CENTRO')]").exists())
                .andExpect(jsonPath("$[?(@.stops[0] == 'Praça Matriz')]").exists());

        // Vote on a route
        mockMvc.perform(post("/passenger/votes")
                        .header("Authorization", "Bearer mock-jwt-token-mariana@siti.com")
                        .header("Role", "USER")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("{\"routeId\":\"%d\",\"stop\":\"Praça Matriz\"}", routeId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));

        // Submit support message
        mockMvc.perform(post("/passenger/support")
                        .header("Authorization", "Bearer mock-jwt-token-mariana@siti.com")
                        .header("Role", "USER")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"subject\":\"Sugestão\",\"message\":\"Adicionar parada\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));
    }

    @Test
    public void testDriverEndpoints() throws Exception {
        // Driver profile (auto-provisions carlos)
        mockMvc.perform(get("/driver/profile")
                        .header("Authorization", "Bearer mock-jwt-token-carlos@siti.com")
                        .header("Role", "DRIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Carlos Motorista")))
                .andExpect(jsonPath("$.cnh", is("12345678901")));

        // Insert a bus for carlos
        Long driverId = jdbcTemplate.queryForObject("SELECT id FROM users WHERE email = 'carlos@siti.com'", Long.class);
        jdbcTemplate.update("INSERT INTO buses (license_plate, bus_model, capacity, accessibility, operation_status, id_administrator) VALUES ('SIT-1010', 'Mercedes Benz', 40, true, 'Ativo', NULL)");
        Long busId = jdbcTemplate.queryForObject("SELECT id FROM buses WHERE license_plate = 'SIT-1010'", Long.class);
        jdbcTemplate.update("INSERT INTO trips (date, status, id_route, id_bus, id_driver) VALUES (CURRENT_DATE, 'Em Andamento', NULL, ?, ?)", busId, driverId);

        // Driver vehicle
        mockMvc.perform(get("/driver/vehicle")
                        .header("Authorization", "Bearer mock-jwt-token-carlos@siti.com")
                        .header("Role", "DRIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.plate", is("SIT-1010")));

        // Report mech failure
        mockMvc.perform(post("/driver/failures")
                        .header("Authorization", "Bearer mock-jwt-token-carlos@siti.com")
                        .header("Role", "DRIVE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"vehiclePlate\":\"SIT-1010\",\"issueType\":\"Motor\",\"severity\":\"Alta\",\"description\":\"Fumaça\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("Registrado")));
    }
}
