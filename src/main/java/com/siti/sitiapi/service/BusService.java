package com.siti.sitiapi.service;

import com.siti.sitiapi.model.Bus;
import com.siti.sitiapi.repository.BusRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BusService {

    private final BusRepository repository;

    public BusService(BusRepository repository) {
        this.repository = repository;
    }

    public Bus create(Bus bus) {
        return repository.save(bus);
    }

    public List<Bus> findAll() {
        return repository.findAll();
    }

    public Bus findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bus not found"));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
