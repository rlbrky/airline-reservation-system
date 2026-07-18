package com.berkay.airline_reservation_system.controller;

import com.berkay.airline_reservation_system.model.Booking;
import com.berkay.airline_reservation_system.service.BookingService;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/bookings")
    public String bookSeat(Long seatId, Principal principal, RedirectAttributes ra) {

        try {

            Booking booking = bookingService.book(principal.getName(), seatId);
            ra.addFlashAttribute("message", "Booking has been successfully booked!");
            ra.addFlashAttribute("bookingReference", booking.getBookingReference());
        } catch (IllegalArgumentException e) {

            ra.addFlashAttribute("error", e.getMessage());
        } catch (ObjectOptimisticLockingFailureException ex) {

            ra.addFlashAttribute("error", "Seat got just booked!");
        }

        return "redirect:/bookings";
    }

    @GetMapping("/bookings")
    public String listCurrentUserBookings(Principal principal, Model model) {

        model.addAttribute("bookings", bookingService.getBookingsForUser(principal.getName()));
        return "bookings";
    }

    @PostMapping("/bookings/{ref}/cancel")
    public String cancelBooking(
            @PathVariable("ref") String bookingRef,
            Principal principal,
            RedirectAttributes ra) {

        try {

            bookingService.cancel(bookingRef, principal.getName());
            ra.addFlashAttribute("message", "Booking has been successfully cancelled!");
            ra.addFlashAttribute("bookingReference", bookingRef);
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/bookings";
    }
}
