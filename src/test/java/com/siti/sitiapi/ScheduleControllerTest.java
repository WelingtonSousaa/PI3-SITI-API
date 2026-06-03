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
class ScheduleControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired ScheduleRepository repository;

    @BeforeEach
    void setUp() { repository.deleteAll(); }

    private Schedule buildSchedule() {
        Schedule s = new Schedule();
        s.setTime("07:30");
        return s;
    }

    @Test
    void shouldCreateSchedule() throws Exception {
        mockMvc.perform(post("/schedules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildSchedule())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.time").value("07:30"));
    }

    @Test
    void shouldListSchedules() throws Exception {
        repository.save(buildSchedule());
        mockMvc.perform(get("/schedules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldGetScheduleById() throws Exception {
        Schedule saved = repository.save(buildSchedule());
        mockMvc.perform(get("/schedules/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()));
    }

    @Test
    void shouldDeleteSchedule() throws Exception {
        Schedule saved = repository.save(buildSchedule());
        mockMvc.perform(delete("/schedules/" + saved.getId()))
                .andExpect(status().isNoContent());
    }
}