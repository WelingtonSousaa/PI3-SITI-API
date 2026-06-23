package com.siti.sitiapi.controller;

import com.siti.sitiapi.dto.LoginRequest;
import com.siti.sitiapi.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestAttribute("userActivate") String userActivate) {
        try {
            Map<String, Object> profile = authService.getUserProfileByEmail(userActivate);
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("erro", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Map<String, Object> response = authService.login(request.getEmail(), request.getPassword());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            String errorMsg = e.getMessage() != null ? e.getMessage() : "Erro desconhecido";
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("erro", errorMsg));
        }
    }
}