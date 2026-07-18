package com.berkay.airline_reservation_system.repository;

import com.berkay.airline_reservation_system.model.Flight;
import com.berkay.airline_reservation_system.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByFlight(Flight flight);

    List<Seat> findByFlightOrderByIdAsc(Flight flight);
}
