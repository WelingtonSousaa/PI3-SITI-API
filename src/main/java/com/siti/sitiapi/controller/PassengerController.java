package com.siti.sitiapi.controller;

import com.siti.sitiapi.dto.PassengerCreateRequest;
import com.siti.sitiapi.dto.PassengerResponse;
import com.siti.sitiapi.service.PassengerService;
import com.siti.sitiapi.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class PassengerController {

    private final PassengerService service;

    @PostMapping("/passengers/create")
    public ResponseEntity<PassengerResponse> create(@RequestBody PassengerCreateRequest request) {
        try {
            return ResponseEntity.ok(service.create(request));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/passenger/routes")
    public ResponseEntity<?> getRoutes(@RequestAttribute("role") String role) {
        if (!"USER".equals(role) && !"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(service.getRoutes());
    }

    @GetMapping("/passenger/profile")
    public ResponseEntity<?> getProfile(@RequestAttribute("userActivate") String email, @RequestAttribute("role") String role) {
        if (!"USER".equals(role) && !"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            return ResponseEntity.ok(service.getProfile(email));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/passenger/votes")
    public ResponseEntity<?> vote(@RequestAttribute("userActivate") String email, @RequestBody Map<String, Object> payload, @RequestAttribute("role") String role) {
        if (!"USER".equals(role) && !"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            return ResponseEntity.ok(service.vote(email, payload));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/passenger/notices")
    public ResponseEntity<?> getNotices(@RequestAttribute("role") String role) {
        if (!"USER".equals(role) && !"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(service.getNotices());
    }

    @GetMapping("/passenger/notices/")
    public ResponseEntity<?> getNoticesTrailing(@RequestAttribute("role") String role) {
        return getNotices(role);
    }

    @GetMapping("/passenger/contacts")
    public ResponseEntity<?> getContacts(@RequestAttribute("userActivate") String email, @RequestAttribute("role") String role) {
        if (!"USER".equals(role) && !"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            return ResponseEntity.ok(service.getContacts(email));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/passenger/support")
    public ResponseEntity<?> submitSupport(@RequestAttribute("userActivate") String email, @RequestBody Map<String, Object> payload, @RequestAttribute("role") String role) {
        if (!"USER".equals(role) && !"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            return ResponseEntity.ok(service.submitSupport(email, payload));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/passenger/photo")
    public ResponseEntity<?> uploadPhoto(@RequestAttribute("userActivate") String email, @RequestAttribute("role") String role) {
        if (!"USER".equals(role) && !"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            return ResponseEntity.ok(service.uploadPhoto(email));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
}
