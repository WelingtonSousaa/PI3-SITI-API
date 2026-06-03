package com.siti.sitiapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.siti.sitiapi.model.*;
import com.siti.sitiapi.repository.*;
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
class TripControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired TripRepository tripRepository;
    @Autowired RouteRepository routeRepository;
    @Autowired BusRepository busRepository;
    @Autowired DriverRepository driverRepository;
    @Autowired AdministratorRepository administratorRepository;

    private Route route;
    private Bus bus;
    private Driver driver;

    @BeforeEach
    void setUp() {
        tripRepository.deleteAll();
        busRepository.deleteAll();
        routeRepository.deleteAll();
        driverRepository.deleteAll();
        administratorRepository.deleteAll();

        Route r = new Route();
        r.setCode("RT-001"); r.setName("Centro - Campus");
        r.setDescription("Rota teste"); r.setStatus("active");
        route = routeRepository.save(r);

        Administrator a = new Administrator();
        a.setEmail("admin@siti.com"); a.setPassword("admin123");
        a.setStatus("active"); a.setIdentifierDocument("98765432100");
        a.setName("Admin"); a.setCity("Fortaleza"); a.setState("CE");
        Administrator admin = administratorRepository.save(a);

        Bus b = new Bus();
        b.setLicensePlate("ABC-1234"); b.setBusModel("Mercedes OF 1721");
        b.setManufacturingYear("2020"); b.setCapacity(42);
        b.setAccessibility(true); b.setOperationStatus("active");
        b.setAdministrator(admin);
        bus = busRepository.save(b);

        Driver d = new Driver();
        d.setName("Pedro"); d.setPhone("85988888888");
        d.setBirthDate(LocalDate.of(1985, 6, 20));
        d.setLicenseNumber("12345678901"); d.setLicenseCategory("D");
        d.setLicenseExpiry(LocalDate.of(2027, 6, 20));
        driver = driverRepository.save(d);
    }

    private Trip buildTrip() {
        Trip t = new Trip();
        t.setDate(LocalDate.of(2026, 6, 10));
        t.setStatus("scheduled");
        t.setRoute(route);
        t.setBus(bus);
        t.setDriver(driver);
        return t;
    }

    @Test
    void shouldCreateTrip() throws Exception {
        mockMvc.perform(post("/trips")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildTrip())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("scheduled"));
    }

    @Test
    void shouldListTrips() throws Exception {
        tripRepository.save(buildTrip());
        mockMvc.perform(get("/trips"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldGetTripById() throws Exception {
        Trip saved = tripRepository.save(buildTrip());
        mockMvc.perform(get("/trips/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()));
    }

    @Test
    void shouldDeleteTrip() throws Exception {
        Trip saved = tripRepository.save(buildTrip());
        mockMvc.perform(delete("/trips/" + saved.getId()))
                .andExpect(status().isNoContent());
    }
}