package com.siti.sitiapi.service;

import com.siti.sitiapi.model.Route;
import com.siti.sitiapi.repository.RouteRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RouteService {

    private final RouteRepository repository;

    public RouteService(RouteRepository repository) {
        this.repository = repository;
    }

    public Route create(Route route) {
        return repository.save(route);
    }

    public List<Route> findAll() {
        return repository.findAll();
    }

    public Route findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Route não encontrada"));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
