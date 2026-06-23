package com.siti.sitiapi.controller;

import com.siti.sitiapi.dto.DriverCreateRequest;
import com.siti.sitiapi.dto.DriverResponse;
import com.siti.sitiapi.exception.BusinessException;
import com.siti.sitiapi.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    @PostMapping("/drivers")
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

    @GetMapping("/driver/routes")
    public ResponseEntity<?> getRoutes(@RequestAttribute("userActivate") String email, @RequestAttribute("role") String role) {
        if (!"DRIVE".equals(role) && !"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            return ResponseEntity.ok(driverService.getRoutes(email));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage() != null ? e.getMessage() : e.getClass().getName()));
        }
    }

    @GetMapping("/driver/profile")
    public ResponseEntity<?> getProfile(@RequestAttribute("userActivate") String email, @RequestAttribute("role") String role) {
        if (!"DRIVE".equals(role) && !"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            return ResponseEntity.ok(driverService.getProfile(email));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage() != null ? e.getMessage() : e.getClass().getName()));
        }
    }

    @GetMapping("/driver/vehicle")
    public ResponseEntity<?> getVehicle(@RequestAttribute("userActivate") String email, @RequestAttribute("role") String role) {
        if (!"DRIVE".equals(role) && !"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            return ResponseEntity.ok(driverService.getVehicle(email));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage() != null ? e.getMessage() : e.getClass().getName()));
        }
    }

    @PutMapping("/driver/routes/{id}/status")
    public ResponseEntity<?> updateTripStatus(@PathVariable Long id, @RequestBody Map<String, Object> payload, @RequestAttribute("role") String role) {
        if (!"DRIVE".equals(role) && !"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            return ResponseEntity.ok(driverService.updateTripStatus(id, payload));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage() != null ? e.getMessage() : e.getClass().getName()));
        }
    }

    @GetMapping("/driver/routes/{routeId}/passengers")
    public ResponseEntity<?> getPassengers(@PathVariable Long routeId, @RequestAttribute("role") String role) {
        if (!"DRIVE".equals(role) && !"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            return ResponseEntity.ok(driverService.getPassengers(routeId));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage() != null ? e.getMessage() : e.getClass().getName()));
        }
    }

    @PutMapping("/driver/passengers/{passengerId}/status")
    public ResponseEntity<?> updatePassengerStatus(@PathVariable Long passengerId, @RequestBody Map<String, Object> payload, @RequestAttribute("role") String role) {
        if (!"DRIVE".equals(role) && !"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            return ResponseEntity.ok(driverService.updatePassengerStatus(passengerId, payload));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage() != null ? e.getMessage() : e.getClass().getName()));
        }
    }

    @PostMapping("/driver/failures")
    public ResponseEntity<?> reportFailure(@RequestBody Map<String, Object> payload, @RequestAttribute("role") String role) {
        if (!"DRIVE".equals(role) && !"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(driverService.reportFailure(payload));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage() != null ? e.getMessage() : e.getClass().getName()));
        }
    }
}
