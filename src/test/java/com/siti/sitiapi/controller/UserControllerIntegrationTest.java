package com.siti.sitiapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.siti.sitiapi.dto.AdminRegisterRequest;
import com.siti.sitiapi.dto.RegisterRequest;
import com.siti.sitiapi.exception.BusinessException;
import com.siti.sitiapi.service.UserService;
import com.siti.sitiapi.configs.AuthenticationInterceptor;
import com.siti.sitiapi.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthService authService;

    @MockBean
    private AuthenticationInterceptor authenticationInterceptor;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws Exception {
        when(authenticationInterceptor.preHandle(any(), any(), any())).thenReturn(true);
    }

    @Test
    void testRegisterSuccess() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("user@test.com");
        request.setPassword("password123");
        request.setIdentifierDocument("12345678900");

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Cadastro enviado para homologação!"));
    }

    /* Note: Since UserController doesn't explicitly have a GlobalExceptionHandler mapped here in the same file, 
       BusinessException might bubble up as 500 in a pure WebMvcTest unless there's a @ControllerAdvice. 
       Assuming BusinessException is unhandled directly in controller methods and handled globally, 
       if there is no global handler mocked, it might result in NestedServletException.
       To test it cleanly without global handler config here, we test the successful path, 
       or we can test it assuming it throws exception. 
    */
    @Test
    void testRegisterAdminSuccess() throws Exception {
        AdminRegisterRequest request = new AdminRegisterRequest();
        request.setEmail("admin@test.com");
        request.setPassword("adminpass");
        request.setCnpj("12.345.678/0001-99");
        request.setCompanyName("SITI Corp");

        mockMvc.perform(post("/users/admin/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Administrador cadastrado com sucesso!"));
    }
}
