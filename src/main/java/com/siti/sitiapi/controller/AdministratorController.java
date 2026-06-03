package com.siti.sitiapi.controller;

import com.siti.sitiapi.model.Administrator;
import com.siti.sitiapi.service.AdministratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/administratores")
public class AdministratorController {

    private final AdministratorService service;

    public AdministratorController(AdministratorService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Administrator> create(@RequestBody Administrator administrator) {
        return ResponseEntity.ok(service.create(administrator));
    }

    @GetMapping
    public ResponseEntity<List<Administrator>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Administrator> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
