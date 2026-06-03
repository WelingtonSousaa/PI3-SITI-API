package com.siti.sitiapi.service;

import com.siti.sitiapi.model.Passenger;
import com.siti.sitiapi.repository.PassengerRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PassengerService {

    private final PassengerRepository repository;

    public PassengerService(PassengerRepository repository) {
        this.repository = repository;
    }

    public Passenger create(Passenger passenger) {
        return repository.save(passenger);
    }

    public List<Passenger> findAll() {
        return repository.findAll();
    }

    public Passenger findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Passenger não encontrado"));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
