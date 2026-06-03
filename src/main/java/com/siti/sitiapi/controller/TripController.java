package com.siti.sitiapi.controller;

import com.siti.sitiapi.model.Trip;
import com.siti.sitiapi.service.TripService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/trips")
public class TripController {

    private final TripService service;

    public TripController(TripService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Trip> create(@RequestBody Trip trip) {
        return ResponseEntity.ok(service.create(trip));
    }

    @GetMapping
    public ResponseEntity<List<Trip>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Trip> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
