package com.siti.sitiapi.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PassengerRequest {
    private Long idUser;
    private LocalDate birthDate;
    private String phone;
    private String type;
    private String registrationNumber;
    private String bondProof;
    private Long idAddress;
}
