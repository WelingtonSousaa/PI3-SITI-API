package com.siti.sitiapi.controller;

import com.siti.sitiapi.model.TransportRequest;
import com.siti.sitiapi.service.TransportRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/transport-requests")
public class TransportRequestController {

    private final TransportRequestService service;

    public TransportRequestController(TransportRequestService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TransportRequest> create(@RequestBody TransportRequest solicitacao) {
        return ResponseEntity.ok(service.create(solicitacao));
    }

    @GetMapping
    public ResponseEntity<List<TransportRequest>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransportRequest> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
