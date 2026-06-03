package com.siti.sitiapi.repository;

import com.siti.sitiapi.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripRepository extends JpaRepository<Trip, Long> {
}
