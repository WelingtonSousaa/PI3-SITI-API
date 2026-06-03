package com.siti.sitiapi.repository;

import com.siti.sitiapi.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
}
