package com.berkay.airline_reservation_system.service;

import com.berkay.airline_reservation_system.model.Flight;
import com.berkay.airline_reservation_system.repository.FlightRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FlightService {

    private final FlightRepository flightRepository;

    public FlightService(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    @Transactional(readOnly = true)
    public List<Flight> listAllFlights() {
        return flightRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Flight> search(String originCode, String destinationCode, LocalDate date) {
        if(originCode.equals(destinationCode)) {
            throw new IllegalArgumentException("Origin and destination must be different.");
        }

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        return flightRepository.search(originCode, destinationCode, start, end);
    }
}
