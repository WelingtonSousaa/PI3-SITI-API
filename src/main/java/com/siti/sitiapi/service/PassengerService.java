package com.siti.sitiapi.service;

import com.siti.sitiapi.dto.ErrorResponse;
import com.siti.sitiapi.dto.PassengerCreateRequest;
import com.siti.sitiapi.dto.PassengerResponse;
import com.siti.sitiapi.exception.BusinessException;
import com.siti.sitiapi.model.Passenger;
import com.siti.sitiapi.model.User;
import com.siti.sitiapi.repository.PassengerRepository;
import com.siti.sitiapi.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class PassengerService {

    private final PassengerRepository passengerRepository;
    private final UserRepository userRepository;

    public PassengerService(PassengerRepository passengerRepository, UserRepository userRepository) {
        this.passengerRepository = passengerRepository;
        this.userRepository = userRepository;
    }

    public PassengerResponse create(PassengerCreateRequest request) {
        User user = userRepository.findById(request.getIdUser());
        if (user == null) {
            throw new BusinessException(
                    new ErrorResponse(400, "Usuário não encontrado para o ID informado.", "/passengers/create")
            );
        }

        if (passengerRepository.existsById(request.getIdUser())) {
            throw new BusinessException(
                    new ErrorResponse(400, "Passageiro já cadastrado para este usuário.", "/passengers/create")
            );
        }

        passengerRepository.create(
                request.getIdUser(),
                request.getBirthDate(),
                request.getPhone(),
                request.getType(),
                request.getRegistrationNumber(),
                request.getBondProof(),
                request.getIdAddress()
        );

        Passenger passenger = passengerRepository.findById(request.getIdUser());

        PassengerResponse response = new PassengerResponse();
        response.setId(passenger.getId());
        response.setEmail(user.getEmail());
        response.setStatus(user.getStatus());
        response.setBirthDate(passenger.getBirthDate());
        response.setPhone(passenger.getPhone());
        response.setType(passenger.getType());
        response.setRegistrationNumber(passenger.getRegistrationNumber());
        response.setBondProof(passenger.getBondProof());
        response.setIdAddress(passenger.getIdAddress());
        return response;
    }
}
