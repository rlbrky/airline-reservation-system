package com.berkay.airline_reservation_system.service;

import com.berkay.airline_reservation_system.model.Airport;
import com.berkay.airline_reservation_system.repository.AirportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AirportService {

    private final AirportRepository airportRepository;

    public AirportService(AirportRepository airportRepository) {
        this.airportRepository = airportRepository;
    }

    @Transactional(readOnly = true)
    public List<Airport> listAllAirports() {
        return airportRepository.findAll();
    }
}
