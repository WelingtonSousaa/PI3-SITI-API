package com.siti.sitiapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.siti.sitiapi.model.Route;
import com.siti.sitiapi.repository.RouteRepository;
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
class RouteControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired RouteRepository repository;

    @BeforeEach
    void setUp() { repository.deleteAll(); }

    private Route buildRoute() {
        Route r = new Route();
        r.setCode("RT-001");
        r.setName("Centro - Campus");
        r.setDescription("Rota do centro ao campus");
        r.setStatus("active");
        return r;
    }

    @Test
    void shouldCreateRoute() throws Exception {
        mockMvc.perform(post("/routes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildRoute())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("RT-001"));
    }

    @Test
    void shouldListRoutes() throws Exception {
        repository.save(buildRoute());
        mockMvc.perform(get("/routes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldGetRouteById() throws Exception {
        Route saved = repository.save(buildRoute());
        mockMvc.perform(get("/routes/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()));
    }

    @Test
    void shouldDeleteRoute() throws Exception {
        Route saved = repository.save(buildRoute());
        mockMvc.perform(delete("/routes/" + saved.getId()))
                .andExpect(status().isNoContent());
    }
}