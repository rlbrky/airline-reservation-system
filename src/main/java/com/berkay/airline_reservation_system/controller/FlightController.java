package com.berkay.airline_reservation_system.controller;

import com.berkay.airline_reservation_system.service.AirportService;
import com.berkay.airline_reservation_system.service.FlightService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
public class FlightController {

    private final FlightService flightService;
    private final AirportService airportService;

    public FlightController(FlightService flightService, AirportService airportService) {
        this.flightService = flightService;
        this.airportService = airportService;
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
}
