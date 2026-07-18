package com.berkay.airline_reservation_system.repository;

import com.berkay.airline_reservation_system.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface FlightRepository extends JpaRepository<Flight, Long> {

    @Query("""
        SELECT f FROM Flight f
        JOIN FETCH f.origin
        JOIN FETCH f.destination
        WHERE f.origin.code = :origin
            AND f.destination.code = :destination
            AND f.departureTime >= :start
            AND f.departureTime < :end
    """)
    List<Flight> search(@Param("origin") String originCode,
                        @Param("destination") String destinationCode,
                        @Param("start") LocalDateTime start,
                        @Param("end") LocalDateTime end);
}
