package com.siti.sitiapi.service;

import com.siti.sitiapi.dto.PassengerCreateRequest;
import com.siti.sitiapi.exception.BusinessException;
import com.siti.sitiapi.model.User;
import com.siti.sitiapi.repository.PassengerRepository;
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
public class PassengerServiceTest {

    @Mock
    private PassengerRepository passengerRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PassengerService passengerService;

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        System.out.println("[TEST] Executing shouldThrowExceptionWhenUserNotFound for PassengerService...");
        PassengerCreateRequest request = new PassengerCreateRequest();
        request.setIdUser(1L);

        when(userRepository.findById(1L)).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            passengerService.create(request);
        });

        System.out.println("       -> Validation caught! Message: " + exception.getError().getMessage());
        assertEquals("Usuário não encontrado para o ID informado.", exception.getError().getMessage());
        verify(passengerRepository, never()).create(any(), any(), any(), any(), any(), any(), any());
        System.out.println("[TEST] Success!");
    }

    @Test
    void shouldThrowExceptionWhenPassengerAlreadyExists() {
        System.out.println("[TEST] Executing shouldThrowExceptionWhenPassengerAlreadyExists for PassengerService...");
        PassengerCreateRequest request = new PassengerCreateRequest();
        request.setIdUser(1L);

        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(user);
        when(passengerRepository.existsById(1L)).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            passengerService.create(request);
        });

        System.out.println("       -> Validation caught! Message: " + exception.getError().getMessage());
        assertEquals("Passageiro já cadastrado para este usuário.", exception.getError().getMessage());
        verify(passengerRepository, never()).create(any(), any(), any(), any(), any(), any(), any());
        System.out.println("[TEST] Success!");
    }
}
