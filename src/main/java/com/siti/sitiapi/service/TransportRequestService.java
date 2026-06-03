package com.siti.sitiapi.service;

import com.siti.sitiapi.model.TransportRequest;
import com.siti.sitiapi.repository.TransportRequestRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TransportRequestService {

    private final TransportRequestRepository repository;

    public TransportRequestService(TransportRequestRepository repository) {
        this.repository = repository;
    }

    public TransportRequest create(TransportRequest solicitacao) {
        return repository.save(solicitacao);
    }

    public List<TransportRequest> findAll() {
        return repository.findAll();
    }

    public TransportRequest findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transport request not found"));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
