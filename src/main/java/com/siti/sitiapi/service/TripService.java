package com.siti.sitiapi.service;

import com.siti.sitiapi.model.Trip;
import com.siti.sitiapi.repository.TripRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TripService {

    private final TripRepository repository;

    public TripService(TripRepository repository) {
        this.repository = repository;
    }

    public Trip create(Trip trip) {
        return repository.save(trip);
    }

    public List<Trip> findAll() {
        return repository.findAll();
    }

    public Trip findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trip não encontrada"));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
