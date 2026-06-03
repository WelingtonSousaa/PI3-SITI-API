package com.siti.sitiapi.repository;

import com.siti.sitiapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
