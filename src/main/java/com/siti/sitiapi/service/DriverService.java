package com.siti.sitiapi.service;
import org.springframework.stereotype.Service;
import com.siti.sitiapi.dto.DriverCreateRequest;
import com.siti.sitiapi.dto.DriverResponse;
import lombok.RequiredArgsConstructor;
import com.siti.sitiapi.repository.DriverRepository;
import com.siti.sitiapi.repository.UserRepository;
import com.siti.sitiapi.model.User;
import com.siti.sitiapi.exception.BusinessException;
import com.siti.sitiapi.dto.ErrorResponse;
import com.siti.sitiapi.model.Driver;

@Service
@RequiredArgsConstructor
public class DriverService {
    private final DriverRepository repository;
    private final UserRepository userRepository;

    public DriverResponse createDriver(DriverCreateRequest request) {
        User user = userRepository.findById(request.getIdUser());
        if (user == null) {
            throw new BusinessException(
                    new ErrorResponse(400, "Usuário não encontrado para o ID informado.", "/drivers/create")
            );
        }
        if (repository.existsById(request.getIdUser())) {
            throw new BusinessException(
                    new ErrorResponse(400, "Driver já cadastrado para este usuário.", "/drivers/create")
            );
        }

        repository.create(
            request.getIdUser(),
            request.getCnhNumber(),
            request.getCnhCategory(),
            request.getName(),
            request.getBirthDate(),
            request.getCnhValidityDate(),
            request.getPhone(),
            request.getIdAddress()
        );
        
        Driver driver = repository.findById(request.getIdUser());
        DriverResponse response = new DriverResponse();
        response.setId(driver.getId());
        response.setCnhNumber(driver.getCnhNumber());
        response.setCnhCategory(driver.getCnhCategory() != null ? driver.getCnhCategory().name() : null);
        response.setName(driver.getName());
        response.setBirthDate(driver.getBirthDate());
        response.setCnhValidityDate(driver.getCnhValidityDate());
        response.setPhone(driver.getPhone());
        response.setIdAddress(driver.getIdAddress());

        return response;
    }
}