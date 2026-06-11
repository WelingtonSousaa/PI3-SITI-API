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
public class UserRegistrationTest {

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
    void testRegisterUserSuccess() throws Exception {
        String email = faker.internet().emailAddress();
        String password = faker.internet().password();
        String doc = faker.number().digits(9);

        String requestBody = String.format("""
            {
                "email": "%s",
                "password": "%s",
                "identifierDocument": "%s"
            }
            """, email, password, doc);

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.email", is(email)));
    }

    @Test
    void testRegisterUserDuplicateEmail() throws Exception {
        String email = faker.internet().emailAddress();
        String password = faker.internet().password();
        String doc = faker.number().digits(9);

        // Register the first user
        String requestBody1 = String.format("""
            {
                "email": "%s",
                "password": "%s",
                "identifierDocument": "%s"
            }
            """, email, password, doc);

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody1))
                .andExpect(status().isOk());

        // Attempt to register the second user with the same email
        String requestBody2 = String.format("""
            {
                "email": "%s",
                "password": "%s",
                "identifierDocument": "%s"
            }
            """, email, faker.internet().password(), faker.number().digits(9));

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody2))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Email já utilizado!")));
    }
}
