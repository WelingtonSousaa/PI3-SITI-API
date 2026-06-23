package com.siti.sitiapi.model;

import lombok.Data;
import java.time.LocalDate;

@Data
public class Passenger {
    private Long id;
    private LocalDate birthDate;
    private String phone;
    private String type;
    private String registrationNumber;
    private String bondProof;
    private String photoUrl;
    private Long idAddress;
    private User user;
}
