package com.siti.sitiapi.service;

import com.siti.sitiapi.model.Driver;
import com.siti.sitiapi.repository.DriverRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DriverService {

    private final DriverRepository repository;

    public DriverService(DriverRepository repository) {
        this.repository = repository;
    }

    public Driver create(Driver driver) {
        return repository.save(driver);
    }

    public List<Driver> findAll() {
        return repository.findAll();
    }

    public Driver findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver não encontrado"));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
