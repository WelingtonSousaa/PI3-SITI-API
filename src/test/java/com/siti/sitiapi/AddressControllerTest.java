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
class AddressControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired AddressRepository repository;

    @BeforeEach
    void setUp() { repository.deleteAll(); }

    private Address buildAddress() {
        Address a = new Address();
        a.setStreet("Rua das Flores");
        a.setBuildingNumber("123");
        a.setNeighborhood("Centro");
        a.setComplement("Apto 10");
        return a;
    }

    @Test
    void shouldCreateAddress() throws Exception {
        mockMvc.perform(post("/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildAddress())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.street").value("Rua das Flores"));
    }

    @Test
    void shouldListAddresses() throws Exception {
        repository.save(buildAddress());
        mockMvc.perform(get("/addresses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldGetAddressById() throws Exception {
        Address saved = repository.save(buildAddress());
        mockMvc.perform(get("/addresses/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()));
    }

    @Test
    void shouldDeleteAddress() throws Exception {
        Address saved = repository.save(buildAddress());
        mockMvc.perform(delete("/addresses/" + saved.getId()))
                .andExpect(status().isNoContent());
    }
}