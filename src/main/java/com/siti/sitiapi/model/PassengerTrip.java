package com.siti.sitiapi.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "passenger_trips")
public class PassengerTrip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_passenger")
    private Passenger passenger;

    @ManyToOne
    @JoinColumn(name = "id_trip")
    private Trip trip;

    @ManyToOne
    @JoinColumn(name = "id_schedule")
    private Schedule schedule;
}