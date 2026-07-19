package com.berkay.airline_reservation_system.service;

import com.berkay.airline_reservation_system.exception.NotFoundException;
import com.berkay.airline_reservation_system.model.Airport;
import com.berkay.airline_reservation_system.model.Flight;
import com.berkay.airline_reservation_system.repository.AirportRepository;
import com.berkay.airline_reservation_system.repository.FlightRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FlightService {

    private final FlightRepository flightRepository;

    private final AirportRepository airportRepository;

    private final SeatService seatService;

    public FlightService(FlightRepository flightRepository, AirportRepository airportRepository, SeatService seatService) {
        this.flightRepository = flightRepository;
        this.airportRepository = airportRepository;
        this.seatService = seatService;
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

    @Transactional(readOnly = true)
    public Flight getById(Long id) {
        return flightRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Flight not found: " + id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public Flight createFlight(String flightNumber, String originCode, String destinationCode,
                               LocalDateTime departure, LocalDateTime arrival, BigDecimal price,
                               int seatRows) {

        Airport origin = airportRepository.findByCode(originCode)
                .orElseThrow(() -> new IllegalArgumentException("Unknown origin: " + originCode));

        Airport destination = airportRepository.findByCode(destinationCode)
                .orElseThrow(() -> new IllegalArgumentException("Unknown destination: " + destinationCode));

        if(origin.equals(destination)) {
            throw new IllegalArgumentException("Origin and destination must be different.");
        }

        if(arrival.isBefore(departure)) {
            throw new IllegalArgumentException("Arrival date cannot be before departure date.");
        }

        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be positive!");
        }

        Flight flight = new Flight();
        flight.setFlightNumber(flightNumber);
        flight.setOrigin(origin);
        flight.setDestination(destination);
        flight.setDepartureTime(departure);
        flight.setArrivalTime(arrival);
        flight.setPrice(price);

        flightRepository.save(flight);
        seatService.generateSeats(flight, seatRows);

        return flight;
    }
}
