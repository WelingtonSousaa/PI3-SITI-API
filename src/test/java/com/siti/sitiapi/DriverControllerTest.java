package com.siti.sitiapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DriverControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired DriverRepository repository;

    @BeforeEach
    void setUp() { repository.deleteAll(); }

    private Driver buildDriver() {
        Driver d = new Driver();
        d.setName("Pedro Motorista");
        d.setPhone("85988888888");
        d.setBirthDate(LocalDate.of(1985, 6, 20));
        d.setLicenseNumber("12345678901");
        d.setLicenseCategory("D");
        d.setLicenseExpiry(LocalDate.of(2027, 6, 20));
        return d;
    }

    @Test
    void shouldCreateDriver() throws Exception {
        mockMvc.perform(post("/drivers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildDriver())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Pedro Motorista"));
    }

    @Test
    void shouldListDrivers() throws Exception {
        repository.save(buildDriver());
        mockMvc.perform(get("/drivers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldGetDriverById() throws Exception {
        Driver saved = repository.save(buildDriver());
        mockMvc.perform(get("/drivers/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()));
    }

    @Test
    void shouldDeleteDriver() throws Exception {
        Driver saved = repository.save(buildDriver());
        mockMvc.perform(delete("/drivers/" + saved.getId()))
                .andExpect(status().isNoContent());
    }
}