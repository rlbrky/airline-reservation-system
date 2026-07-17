package com.berkay.airline_reservation_system.repository;

import com.berkay.airline_reservation_system.model.Airport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AirportRepository extends JpaRepository<Airport, Long> {

    Optional<Airport> findByCode(String code);
}
