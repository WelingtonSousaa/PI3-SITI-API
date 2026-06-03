package com.siti.sitiapi.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "buses")
public class Bus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String licensePlate;
    private String busModel;
    private String manufacturingYear;
    private Integer capacity;
    private Boolean accessibility;
    private String operationStatus;

    @ManyToOne
    @JoinColumn(name = "id_administrator")
    private Administrator administrator;
}