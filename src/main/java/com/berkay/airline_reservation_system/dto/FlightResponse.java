package com.berkay.airline_reservation_system.dto;

import com.berkay.airline_reservation_system.model.Flight;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FlightResponse(Long id, String flightNumber, String origin, String destination,
                             LocalDateTime departureTime, LocalDateTime arrivalTime, BigDecimal price) {

    public static FlightResponse from(Flight flight) {
        return new FlightResponse(flight.getId(), flight.getFlightNumber(), flight.getOrigin().getCode(), flight.getDestination().getCode(),
                flight.getDepartureTime(), flight.getArrivalTime(), flight.getPrice());
    }
}
