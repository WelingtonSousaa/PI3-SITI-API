package com.siti.sitiapi.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "drivers")
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String licenseNumber;
    private String licenseCategory;
    private String name;
    private LocalDate birthDate;
    private String phone;
    private LocalDate licenseExpiry;
}