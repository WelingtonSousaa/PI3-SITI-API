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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdministratorControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired AdministratorRepository repository;

    @BeforeEach
    void setUp() { repository.deleteAll(); }

    private Administrator buildAdministrator() {
        Administrator a = new Administrator();
        a.setEmail("admin@siti.com");
        a.setPassword("admin123");
        a.setStatus("active");
        a.setIdentifierDocument("98765432100");
        a.setName("Carlos Admin");
        a.setCity("Fortaleza");
        a.setState("CE");
        return a;
    }

    @Test
    void shouldCreateAdministrator() throws Exception {
        mockMvc.perform(post("/administrators")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildAdministrator())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("admin@siti.com"));
    }

    @Test
    void shouldListAdministrators() throws Exception {
        repository.save(buildAdministrator());
        mockMvc.perform(get("/administrators"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldGetAdministratorById() throws Exception {
        Administrator saved = repository.save(buildAdministrator());
        mockMvc.perform(get("/administrators/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()));
    }

    @Test
    void shouldDeleteAdministrator() throws Exception {
        Administrator saved = repository.save(buildAdministrator());
        mockMvc.perform(delete("/administrators/" + saved.getId()))
                .andExpect(status().isNoContent());
    }
}