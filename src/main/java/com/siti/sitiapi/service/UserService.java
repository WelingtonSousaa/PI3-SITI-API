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
        if (repository.existsByEmail(request.getEmail())) {
            throw new BusinessException(
                    new ErrorResponse(400, "Email já utilizado!", "/users/register")
            );
        }

        String name = com.siti.sitiapi.service.AuthService.formatNameFromEmail(request.getEmail());

        repository.create(
                request.getEmail(),
                request.getPassword(),
                request.getIdentifierDocument(),
                name
        );

        User user = repository.findByEmail(request.getEmail());

        RegisterResponse response = new RegisterResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        return response;
    }
}