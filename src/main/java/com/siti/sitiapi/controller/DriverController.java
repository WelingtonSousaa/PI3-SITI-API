package com.siti.sitiapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import com.siti.sitiapi.exception.BusinessException;

import com.siti.sitiapi.dto.DriverCreateRequest;
import com.siti.sitiapi.dto.DriverResponse;
import com.siti.sitiapi.service.DriverService;

@RestController
@RequestMapping("/drivers")
@CrossOrigin(origins = "*")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @PostMapping
    public ResponseEntity<DriverResponse> createDriver(
            @RequestBody DriverCreateRequest request,
            @RequestAttribute("role") String role) {

        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            return ResponseEntity.ok(driverService.createDriver(request));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}
