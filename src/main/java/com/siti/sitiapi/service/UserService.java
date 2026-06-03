package com.siti.sitiapi.service;

import com.siti.sitiapi.dto.ErrorResponse;
import com.siti.sitiapi.dto.RegisterRequest;
import com.siti.sitiapi.dto.RegisterResponse;
import com.siti.sitiapi.exception.BusinessException;
import com.siti.sitiapi.model.User;
import com.siti.sitiapi.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.UUID;
@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public RegisterResponse register(RegisterRequest request) {
        User existing = repository.findByEmail(request.getEmail());
        if (existing != null) {
            throw new RuntimeException("Email já utilizado!");
        }

        String apiKey = UUID.randomUUID().toString();
        repository.create(request.getEmail(), request.getPassword(),
                request.getIdentifierDocument(), apiKey);

        RegisterResponse response = new RegisterResponse();
        response.setEmail(request.getEmail());
        response.setApiKey(apiKey);
        return response;
    }
}