package com.siti.sitiapi.controller;

import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserLoginTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final Faker faker = new Faker();

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM administrators");
        jdbcTemplate.execute("DELETE FROM users");
    }

    @Test
    void testLoginUserSuccess() throws Exception {
        String email = faker.internet().emailAddress();
        String password = faker.internet().password();
        String doc = faker.number().digits(9);

        // Register the user first
        String registerBody = String.format("""
            {
                "email": "%s",
                "password": "%s",
                "identifierDocument": "%s"
            }
            """, email, password, doc);

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isOk());

        // Perform login
        String loginBody = String.format("""
            {
                "email": "%s",
                "password": "%s"
            }
            """, email, password);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessKey", notNullValue()))
                .andExpect(jsonPath("$.role", is("USER")));
    }

    @Test
    void testLoginAdminSuccess() throws Exception {
        String email = faker.internet().emailAddress();
        String password = faker.internet().password();
        String doc = faker.number().digits(9);

        // Register the user first
        String registerBody = String.format("""
            {
                "email": "%s",
                "password": "%s",
                "identifierDocument": "%s"
            }
            """, email, password, doc);

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isOk());

        // Retrieve the registered user's ID
        Long userId = jdbcTemplate.queryForObject(
                "SELECT id FROM users WHERE email = ?", Long.class, email);

        // Associate this user as an administrator
        jdbcTemplate.update(
                "INSERT INTO administrators (id, name, city, state, id_address) VALUES (?, ?, 'City', 'State', NULL)",
                userId, faker.name().fullName()
        );

        // Perform login
        String loginBody = String.format("""
            {
                "email": "%s",
                "password": "%s"
            }
            """, email, password);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessKey", notNullValue()))
                .andExpect(jsonPath("$.role", is("ADMIN")));
    }

    @Test
    void testLoginFailureInvalidCredentials() throws Exception {
        String email = faker.internet().emailAddress();
        String password = faker.internet().password();

        String loginBody = String.format("""
            {
                "email": "%s",
                "password": "%s"
            }
            """, email, password);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.erro", is("Usuário ou senha incorretos.")));
    }
}
