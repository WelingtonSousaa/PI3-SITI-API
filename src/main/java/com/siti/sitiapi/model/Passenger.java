package com.siti.sitiapi.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "passengers")
@PrimaryKeyJoinColumn(name = "id")
public class Passenger extends User {

    private LocalDate birthDate;
    private String phone;
    private String type;
    private String registrationNumber;
    private String bondProof;

    @ManyToOne
    @JoinColumn(name = "id_address")
    private Address address;
}