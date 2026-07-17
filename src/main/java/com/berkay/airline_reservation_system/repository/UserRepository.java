package com.berkay.airline_reservation_system.repository;

import com.berkay.airline_reservation_system.model.AirlineUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<AirlineUser, Long> {

    Optional<AirlineUser> findByUsername(String username);
}
