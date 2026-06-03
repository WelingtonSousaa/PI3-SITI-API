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
class PassengerControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired PassengerRepository repository;

    @BeforeEach
    void setUp() { repository.deleteAll(); }

    private Passenger buildPassenger() {
        Passenger p = new Passenger();
        p.setEmail("joao@email.com");
        p.setPassword("123456");
        p.setStatus("active");
        p.setIdentifierDocument("12345678900");
        p.setBirthDate(LocalDate.of(2000, 1, 15));
        p.setPhone("85999999999");
        p.setRegistrationNumber("MAT001");
        p.setType("student");
        return p;
    }

    @Test
    void shouldCreatePassenger() throws Exception {
        mockMvc.perform(post("/passengers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildPassenger())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("joao@email.com"));
    }

    @Test
    void shouldListPassengers() throws Exception {
        repository.save(buildPassenger());
        mockMvc.perform(get("/passengers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldGetPassengerById() throws Exception {
        Passenger saved = repository.save(buildPassenger());
        mockMvc.perform(get("/passengers/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()));
    }

    @Test
    void shouldDeletePassenger() throws Exception {
        Passenger saved = repository.save(buildPassenger());
        mockMvc.perform(delete("/passengers/" + saved.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn404WhenPassengerNotFound() throws Exception {
        mockMvc.perform(get("/passengers/999"))
                .andExpect(status().is5xxServerError());
    }
}