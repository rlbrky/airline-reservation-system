package com.berkay.airline_reservation_system.controller;

import com.berkay.airline_reservation_system.exception.NotFoundException;
import com.berkay.airline_reservation_system.model.Airport;
import com.berkay.airline_reservation_system.model.Flight;
import com.berkay.airline_reservation_system.service.AirportService;
import com.berkay.airline_reservation_system.service.BookingService;
import com.berkay.airline_reservation_system.service.FlightService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final FlightService flightService;

    private final AirportService airportService;

    private final BookingService bookingService;

    public AdminController(FlightService flightService, AirportService airportService, BookingService bookingService) {
        this.flightService = flightService;
        this.airportService = airportService;
        this.bookingService = bookingService;
    }

    // Runs before every handler and feeds airport list to model. This way we always have access to airports
    @ModelAttribute("airports")
    public List<Airport> airports() {
        return airportService.listAllAirports();
    }

    @ModelAttribute("flights")
    public List<Flight> flights() {
        return flightService.listAllFlights();
    }

    @GetMapping("/flights")
    public String flights(Model model) {

        //model.addAttribute("flights", flightService.listAllFlights());
        model.addAttribute("flightForm", new FlightForm());
        //model.addAttribute("airports", airportService.listAllAirports()); -> no need for this anymore

        return "admin/flights";
    }

    @PostMapping("/flights")
    public String createFlight(@Valid @ModelAttribute("flightForm") FlightForm flightForm,
                               BindingResult bindingResult,
                               RedirectAttributes ra) {

        if(bindingResult.hasErrors()) {
            return "admin/flights";
        }

        try {
            flightService.createFlight(flightForm.getFlightNumber(), flightForm.getOriginCode(),
                    flightForm.getDestinationCode(), flightForm.getDepartureTime(),
                    flightForm.getArrivalTime(), flightForm.getPrice(), flightForm.getSeatRows());

            ra.addFlashAttribute("message", "Flight created successfully.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/flights";
        }

        return "redirect:/admin/flights";
    }

    @GetMapping("/airports")
    public String airports(Model model) {

        // airport list is given already
        model.addAttribute("airportForm", new AirportForm());

        return "admin/airports";
    }

    @PostMapping("/airports")
    public String createAirport(@Valid @ModelAttribute("airportForm") AirportForm airportForm,
                                BindingResult bindingResult,
                                RedirectAttributes ra) {

        if(bindingResult.hasErrors()) {
            return "airports";
        }

        try {

            airportService.createAirport(airportForm.getCode(), airportForm.getName(), airportForm.getCity());
            ra.addFlashAttribute("message", "Airport created successfully.");

        } catch (IllegalArgumentException e) {

            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/airports";
        }

        return "redirect:/admin/airports";
    }

    @GetMapping("/bookings")
    public String bookings(Model model) {

        model.addAttribute("bookings", bookingService.getAllBookingsNewestFirst());

        return "admin/bookings";
    }

    @PostMapping("/bookings/{ref}/cancel")
    public String cancelBooking(
            @PathVariable("ref") String ref,
            RedirectAttributes ra) {

        try {

            bookingService.adminCancel(ref);
            ra.addFlashAttribute("message", "Booking has been cancelled.");
            ra.addFlashAttribute("bookingReference", ref);

        } catch (NotFoundException e) {
            ra.addFlashAttribute("error", e.getMessage());
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/bookings";
    }
}
