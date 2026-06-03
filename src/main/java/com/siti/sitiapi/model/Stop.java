package com.siti.sitiapi.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "stops")
public class Stop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String status;

    @ManyToOne
    @JoinColumn(name = "id_route")
    private Route route;

    @ManyToOne
    @JoinColumn(name = "id_address")
    private Address address;

    @ManyToOne
    @JoinColumn(name = "id_schedule")
    private Schedule schedule;
}