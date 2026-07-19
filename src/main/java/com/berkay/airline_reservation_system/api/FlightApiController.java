package com.berkay.airline_reservation_system.api;

import com.berkay.airline_reservation_system.dto.FlightResponse;
import com.berkay.airline_reservation_system.dto.SeatResponse;
import com.berkay.airline_reservation_system.model.Flight;
import com.berkay.airline_reservation_system.service.FlightService;
import com.berkay.airline_reservation_system.service.SeatService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/flights")
public class FlightApiController {

    private final FlightService flightService;

    private final SeatService seatService;

    public FlightApiController(FlightService flightService, SeatService seatService) {
        this.flightService = flightService;
        this.seatService = seatService;
    }

    @GetMapping
    public List<FlightResponse> getFlights(@RequestParam(required = false) String origin,
                                           @RequestParam(required = false) String destination,
                                           @RequestParam(required = false)
                                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        if(origin != null && destination != null && date != null) {

            return flightService.search(origin, destination, date)
                    .stream().map(FlightResponse::from).toList();
        } else {
            return flightService.listAllFlights()
                    .stream().map(FlightResponse::from).toList();
        }
    }

    @GetMapping("/{id}")
    public FlightResponse getFlight(@PathVariable Long id) {

        return FlightResponse.from(flightService.getById(id));
    }

    @GetMapping("/{id}/seats")
    public List<SeatResponse> getSeats(@PathVariable Long id) {

        Flight flight = flightService.getById(id);

        return seatService.getSeatsByFlightOrderByIdAsc(flight)
                .stream().map(SeatResponse::from).toList();
    }
}
