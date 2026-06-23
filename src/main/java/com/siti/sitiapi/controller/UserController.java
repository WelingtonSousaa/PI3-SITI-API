package com.siti.sitiapi.controller;

import com.siti.sitiapi.dto.RegisterRequest;
import com.siti.sitiapi.dto.RegisterResponse;
import com.siti.sitiapi.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        service.register(request);
        return org.springframework.http.ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(java.util.Map.of(
                "success", true,
                "message", "Cadastro enviado para homologação!"
        ));
    }

}