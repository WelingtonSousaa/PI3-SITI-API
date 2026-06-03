package com.siti.sitiapi.repository;

import com.siti.sitiapi.model.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PassengerRepository extends JpaRepository<Passenger, Long> {
}
