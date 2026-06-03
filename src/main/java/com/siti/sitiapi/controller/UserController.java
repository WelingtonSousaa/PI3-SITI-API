package com.siti.sitiapi.controller;

import com.siti.sitiapi.dto.RegisterRequest;
import com.siti.sitiapi.dto.RegisterResponse;
import com.siti.sitiapi.model.User;
import com.siti.sitiapi.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }


    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(service.register(request));
    }

}