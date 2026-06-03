package com.siti.sitiapi.service;

import com.siti.sitiapi.model.Administrator;
import com.siti.sitiapi.repository.AdministratorRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AdministratorService {

    private final AdministratorRepository repository;

    public AdministratorService(AdministratorRepository repository) {
        this.repository = repository;
    }

    public Administrator create(Administrator administrator) {
        return repository.save(administrator);
    }

    public List<Administrator> findAll() {
        return repository.findAll();
    }

    public Administrator findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Administrator não encontrado"));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
