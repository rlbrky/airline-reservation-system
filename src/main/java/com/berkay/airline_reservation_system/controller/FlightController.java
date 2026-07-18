package com.berkay.airline_reservation_system.controller;

import com.berkay.airline_reservation_system.model.Flight;
import com.berkay.airline_reservation_system.model.SeatStatus;
import com.berkay.airline_reservation_system.service.AirportService;
import com.berkay.airline_reservation_system.service.FlightService;
import com.berkay.airline_reservation_system.service.SeatService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
public class FlightController {

    private final FlightService flightService;

    private final AirportService airportService;

    private final SeatService seatService;

    public FlightController(FlightService flightService, AirportService airportService, SeatService seatService) {
        this.flightService = flightService;
        this.airportService = airportService;
        this.seatService = seatService;
    }

    @GetMapping("/flights")
    public String flights(
            @RequestParam(required = false) String origin,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model) {

        model.addAttribute("airports", airportService.listAllAirports()); // always needed for dropdowns
        if(origin != null && destination != null && date != null) {

            try {
                model.addAttribute("flights", flightService.search(origin, destination, date));
            } catch (IllegalArgumentException e) {
                model.addAttribute("error", e.getMessage());
            }
        }

        // echo origin/destination/date back so the form stays filled
        model.addAttribute("origin", origin);
        model.addAttribute("destination", destination);
        model.addAttribute("date", date);

        return "flights/search";
    }

    @GetMapping("/flights/{id}")
    public String flightDetails(
            @PathVariable Long id,
            Model model
    ) {

        Flight flight = flightService.getById(id);

        model.addAttribute("flight", flight);
        model.addAttribute("seats", seatService.getSeatsByFlightOrderByIdAsc(flight));
        model.addAttribute("availableSeatCount", seatService.countByFlightAndSeatStatus(flight, SeatStatus.AVAILABLE));

        return "flights/details";
    }
}
