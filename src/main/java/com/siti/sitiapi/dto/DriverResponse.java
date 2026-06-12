package com.siti.sitiapi.dto;
import java.time.LocalDate;
import lombok.Data;

@Data
public class DriverResponse {
    private Long id;
    private String cnhNumber;
    private String cnhCategory;
    private String name;
    private LocalDate birthDate;
    private LocalDate cnhValidityDate;
    private String phone;
    private Long idAddress;
}
