package com.siti.sitiapi.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "trips")
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;
    private String status;

    @ManyToOne
    @JoinColumn(name = "id_route")
    private Route route;

    @ManyToOne
    @JoinColumn(name = "id_bus")
    private Bus bus;

    @ManyToOne
    @JoinColumn(name = "id_driver")
    private Driver driver;
}