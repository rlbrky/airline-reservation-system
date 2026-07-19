package com.berkay.airline_reservation_system.api;

import com.berkay.airline_reservation_system.dto.BookingRequest;
import com.berkay.airline_reservation_system.dto.BookingResponse;
import com.berkay.airline_reservation_system.model.Booking;
import com.berkay.airline_reservation_system.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingApiController {

    private final BookingService bookingService;

    public BookingApiController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingResponse> book(@Valid @RequestBody BookingRequest bookingRequest, Principal principal) {

        Booking booking = bookingService.book(principal.getName(), bookingRequest.seatId());
        URI location = URI.create("/api/bookings/" + booking.getBookingReference());

        return ResponseEntity.created(location).body(BookingResponse.from(booking));
    }

    @GetMapping
    public List<BookingResponse> getBookingsForUser(Principal principal) {

        return bookingService.getBookingsForUser(principal.getName())
                .stream().map(BookingResponse::from).toList();
    }

    @DeleteMapping("/{ref}")
    public ResponseEntity<Void> cancelBooking(Principal principal, @PathVariable String ref) {

        bookingService.cancel(ref, principal.getName());
        return ResponseEntity.noContent().build();
    }
}
