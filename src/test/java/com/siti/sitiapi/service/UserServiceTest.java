package com.siti.sitiapi.service;

import com.siti.sitiapi.dto.AdminRegisterRequest;
import com.siti.sitiapi.dto.RegisterRequest;
import com.siti.sitiapi.dto.RegisterResponse;
import com.siti.sitiapi.exception.BusinessException;
import com.siti.sitiapi.model.User;
import com.siti.sitiapi.repository.UserRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private Faker faker;

    @BeforeEach
    void setUp() {
        faker = new Faker(new Locale("pt", "BR"));
    }

    @Test
    void testRegisterSuccess() {
        // Arrange
        String email = faker.internet().emailAddress();
        RegisterRequest request = new RegisterRequest();
        request.setEmail(email);
        request.setPassword("password123");
        request.setIdentifierDocument(faker.cpf().valid());

        when(userRepository.existsByEmail(email)).thenReturn(false);
        User mockedUser = new User();
        mockedUser.setId(1L);
        mockedUser.setEmail(email);
        when(userRepository.findByEmail(email)).thenReturn(mockedUser);

        // Act
        RegisterResponse response = userService.register(request);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(email, response.getEmail());
        verify(userRepository, times(1)).create(eq(email), eq("password123"), anyString(), anyString());
    }

    @Test
    void testRegisterEmailAlreadyExistsThrowsException() {
        // Arrange
        String email = faker.internet().emailAddress();
        RegisterRequest request = new RegisterRequest();
        request.setEmail(email);

        when(userRepository.existsByEmail(email)).thenReturn(true);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.register(request);
        });

        assertEquals(400, exception.getError().getStatus());
        assertEquals("Email já utilizado!", exception.getError().getMessage());
    }

    @Test
    void testRegisterAdminSuccess() {
        // Arrange
        String email = faker.internet().emailAddress();
        AdminRegisterRequest request = new AdminRegisterRequest();
        request.setEmail(email);
        request.setPassword("adminpass");
        request.setCnpj(faker.cnpj().valid());
        request.setCompanyName(faker.company().name());
        request.setCity(faker.address().city());
        request.setState(faker.address().stateAbbr());

        when(userRepository.existsByEmail(email)).thenReturn(false);
        User mockedUser = new User();
        mockedUser.setId(2L);
        mockedUser.setEmail(email);
        when(userRepository.findByEmail(email)).thenReturn(mockedUser);

        // Act
        RegisterResponse response = userService.registerAdmin(request);

        // Assert
        assertNotNull(response);
        assertEquals(2L, response.getId());
        assertEquals(email, response.getEmail());
        verify(userRepository, times(1)).createAdmin(
                eq(email), eq("adminpass"), eq(request.getCnpj()), eq(request.getCompanyName()), eq(request.getCity()), eq(request.getState())
        );
    }

    @Test
    void testRegisterAdminEmailAlreadyExistsThrowsException() {
        // Arrange
        String email = faker.internet().emailAddress();
        AdminRegisterRequest request = new AdminRegisterRequest();
        request.setEmail(email);

        when(userRepository.existsByEmail(email)).thenReturn(true);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.registerAdmin(request);
        });

        assertEquals(400, exception.getError().getStatus());
        assertEquals("Email já utilizado!", exception.getError().getMessage());
    }
}
