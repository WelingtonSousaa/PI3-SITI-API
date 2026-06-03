package com.siti.sitiapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.siti.sitiapi.model.Administrator;
import com.siti.sitiapi.model.Bus;
import com.siti.sitiapi.repository.AdministratorRepository;
import com.siti.sitiapi.repository.BusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BusControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired BusRepository repository;
    @Autowired AdministratorRepository administratorRepository;

    private Administrator admin;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        administratorRepository.deleteAll();
        Administrator a = new Administrator();
        a.setEmail("admin@siti.com");
        a.setPassword("admin123");
        a.setStatus("active");
        a.setIdentifierDocument("98765432100");
        a.setName("Admin");
        a.setCity("Fortaleza");
        a.setState("CE");
        admin = administratorRepository.save(a);
    }

    private Bus buildBus() {
        Bus b = new Bus();
        b.setLicensePlate("ABC-1234");
        b.setBusModel("Mercedes OF 1721");
        b.setManufacturingYear("2020");
        b.setCapacity(42);
        b.setAccessibility(true);
        b.setOperationStatus("active");
        b.setAdministrator(admin);
        return b;
    }

    @Test
    void shouldCreateBus() throws Exception {
        mockMvc.perform(post("/buses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildBus())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.licensePlate").value("ABC-1234"));
    }

    @Test
    void shouldListBuses() throws Exception {
        repository.save(buildBus());
        mockMvc.perform(get("/buses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldGetBusById() throws Exception {
        Bus saved = repository.save(buildBus());
        mockMvc.perform(get("/buses/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()));
    }

    @Test
    void shouldDeleteBus() throws Exception {
        Bus saved = repository.save(buildBus());
        mockMvc.perform(delete("/buses/" + saved.getId()))
                .andExpect(status().isNoContent());
    }
}