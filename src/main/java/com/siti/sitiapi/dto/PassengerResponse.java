package com.siti.sitiapi.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PassengerResponse {
    private Long id;
    private String email;
    private String status;
    private LocalDate birthDate;
    private String phone;
    private String type;
    private String registrationNumber;
    private String bondProof;
    private Long idAddress;
}
