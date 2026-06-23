package com.siti.sitiapi.service;

import com.siti.sitiapi.dto.DriverCreateRequest;
import com.siti.sitiapi.exception.BusinessException;
import com.siti.sitiapi.model.User;
import com.siti.sitiapi.repository.DriverRepository;
import com.siti.sitiapi.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DriverServiceTest {

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DriverService driverService;

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        System.out.println("[TEST] Executing shouldThrowExceptionWhenUserNotFound for DriverService...");
        DriverCreateRequest request = new DriverCreateRequest();
        request.setIdUser(1L);

        when(userRepository.findById(1L)).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            driverService.createDriver(request);
        });

        System.out.println("       -> Validation caught! Message: " + exception.getError().getMessage());
        assertEquals("Usuário não encontrado para o ID informado.", exception.getError().getMessage());
        verify(driverRepository, never()).create(any(), any(), any(), any(), any(), any(), any(), any());
        System.out.println("[TEST] Success!");
    }

    @Test
    void shouldThrowExceptionWhenDriverAlreadyExists() {
        System.out.println("[TEST] Executing shouldThrowExceptionWhenDriverAlreadyExists for DriverService...");
        DriverCreateRequest request = new DriverCreateRequest();
        request.setIdUser(1L);

        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(user);
        when(driverRepository.existsById(1L)).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            driverService.createDriver(request);
        });

        System.out.println("       -> Validation caught! Message: " + exception.getError().getMessage());
        assertEquals("Driver já cadastrado para este usuário.", exception.getError().getMessage());
        verify(driverRepository, never()).create(any(), any(), any(), any(), any(), any(), any(), any());
        System.out.println("[TEST] Success!");
    }
}
