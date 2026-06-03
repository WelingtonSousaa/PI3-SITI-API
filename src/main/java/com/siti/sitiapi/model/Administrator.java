package com.siti.sitiapi.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "administrators")
@PrimaryKeyJoinColumn(name = "id")
public class Administrator extends User {

    private String name;
    private String city;
    private String state;

    @ManyToOne
    @JoinColumn(name = "id_address")
    private Address address;
}