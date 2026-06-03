package com.siti.sitiapi.repository;

import com.siti.sitiapi.model.PassengerTrip;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PassengerTripRepository extends JpaRepository<PassengerTrip, Long> {
}
