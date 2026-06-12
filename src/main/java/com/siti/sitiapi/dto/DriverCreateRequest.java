package com.siti.sitiapi.dto;
import java.time.LocalDate;
import lombok.Data;

@Data
public class DriverCreateRequest {
    private String cnhNumber;
    private String cnhCategory;
    private String name;
    private LocalDate birthDate;
    private LocalDate cnhValidityDate;
    private String phone;
    private Long idUser;
    private Long idAddress;
}
