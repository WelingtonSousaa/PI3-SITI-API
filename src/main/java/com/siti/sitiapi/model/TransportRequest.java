package com.siti.sitiapi.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "transport_requests")
public class TransportRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate day;
    private String schedule;
    private Boolean needsAccessibility;
    private String destination;

    @ManyToOne
    @JoinColumn(name = "id_passenger")
    private Passenger passenger;

    @ManyToOne
    @JoinColumn(name = "id_bus")
    private Bus bus;
}