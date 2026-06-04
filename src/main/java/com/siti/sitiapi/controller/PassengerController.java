package com.siti.sitiapi.controller;

import com.siti.sitiapi.dto.PassengerCreateRequest;
import com.siti.sitiapi.dto.PassengerResponse;
import com.siti.sitiapi.service.PassengerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/passengers")
public class PassengerController {

    private final PassengerService service;

    public PassengerController(PassengerService service) {
        this.service = service;
    }

    @PostMapping("/create")
    public ResponseEntity<PassengerResponse> create(@RequestBody PassengerCreateRequest request) {
        return ResponseEntity.ok(service.create(request));
    }
}
