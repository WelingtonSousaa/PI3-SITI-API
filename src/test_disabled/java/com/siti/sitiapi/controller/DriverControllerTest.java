package com.siti.sitiapi.controller;

import com.siti.sitiapi.dto.DriverCreateRequest;
import com.siti.sitiapi.dto.DriverResponse;
import com.siti.sitiapi.exception.BusinessException;
import com.siti.sitiapi.dto.ErrorResponse;
import com.siti.sitiapi.service.DriverService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DriverControllerTest {

    @Mock
    private DriverService driverService;

    @InjectMocks
    private DriverController driverController;

    @Test
    void shouldReturnForbiddenWhenRoleIsNotAdmin() {
        DriverCreateRequest request = new DriverCreateRequest();
        
        ResponseEntity<DriverResponse> response = driverController.createDriver(request, "USER");
        
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(driverService, never()).createDriver(any());
    }

    @Test
    void shouldReturnBadRequestWhenServiceThrowsBusinessException() {
        DriverCreateRequest request = new DriverCreateRequest();
        
        when(driverService.createDriver(request)).thenThrow(new BusinessException(new ErrorResponse(400, "Error", "path")));

        ResponseEntity<DriverResponse> response = driverController.createDriver(request, "ADMIN");
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(driverService, times(1)).createDriver(request);
    }
}
