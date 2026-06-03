package com.siti.sitiapi.controller;

import com.siti.sitiapi.model.Passenger;
import com.siti.sitiapi.service.PassengerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/passengers")
public class PassengerController {

    private final PassengerService service;

    public PassengerController(PassengerService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Passenger> create(@RequestBody Passenger passenger) {
        return ResponseEntity.ok(service.create(passenger));
    }

    @GetMapping
    public ResponseEntity<List<Passenger>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Passenger> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
