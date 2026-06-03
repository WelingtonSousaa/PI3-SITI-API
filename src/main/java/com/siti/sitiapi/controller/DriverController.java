package com.siti.sitiapi.controller;

import com.siti.sitiapi.model.Driver;
import com.siti.sitiapi.service.DriverService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/drivers")
public class DriverController {

    private final DriverService service;

    public DriverController(DriverService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Driver> create(@RequestBody Driver driver) {
        return ResponseEntity.ok(service.create(driver));
    }

    @GetMapping
    public ResponseEntity<List<Driver>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Driver> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
