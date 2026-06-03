package com.siti.sitiapi.controller;

import com.siti.sitiapi.model.Bus;
import com.siti.sitiapi.service.BusService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/bus")
public class BusController {

    private final BusService service;

    public BusController(BusService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Bus> create(@RequestBody Bus bus) {
        return ResponseEntity.ok(service.create(bus));
    }

    @GetMapping
    public ResponseEntity<List<Bus>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bus> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
