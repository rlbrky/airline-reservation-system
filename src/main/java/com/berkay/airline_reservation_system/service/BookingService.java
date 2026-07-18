package com.berkay.airline_reservation_system.service;

import com.berkay.airline_reservation_system.model.*;
import com.berkay.airline_reservation_system.repository.BookingRepository;
import com.berkay.airline_reservation_system.repository.SeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.List;

@Service
public class BookingService {

    private final UserService userService;

    private final SeatRepository seatRepository;

    private final BookingRepository bookingRepository;

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private final SecureRandom random = new SecureRandom();

    public BookingService(UserService userService, SeatRepository seatRepository, BookingRepository bookingRepository) {
        this.userService = userService;
        this.seatRepository = seatRepository;
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    public Booking book(String username, Long seatId) {

        AirlineUser user = userService.getByUsername(username);
        Seat seat = seatRepository.findById(seatId).orElseThrow(() -> new IllegalArgumentException("Seat not found: " + seatId));

        if(seat.getSeatStatus() == SeatStatus.BOOKED){
            throw new IllegalArgumentException("Seat is already booked");
        }

        seat.setSeatStatus(SeatStatus.BOOKED);

        Booking booking = new Booking();

        booking.setUser(user);
        booking.setSeat(seat);
        booking.setBookingReference(generateBookingReference());
        booking.setFlight(seat.getFlight());
        booking.setBookingStatus(BookingStatus.CONFIRMED);
        booking.setBookedAt(Instant.now());
        booking.setPricePaid(seat.getFlight().getPrice());

        bookingRepository.save(booking);

        return booking;
    }

    @Transactional
    public void cancel(String bookingRef, String username) {

        Booking booking = bookingRepository.findByBookingReference(bookingRef)
                .orElseThrow(() -> new IllegalArgumentException("No such booking: " + bookingRef));

        if(!booking.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("You can only cancel your own bookings!");
        }
        if(booking.getBookingStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalArgumentException("Booking is not active!");
        }

        booking.setBookingStatus(BookingStatus.CANCELED);
        booking.getSeat().setSeatStatus(SeatStatus.AVAILABLE);
    }

    @Transactional(readOnly = true)
    public List<Booking> getBookingsForUser(String username) {

        AirlineUser user = userService.getByUsername(username);
        return bookingRepository.findByUserOrderByBookedAtDesc(user);
    }

    private String generateBookingReference() {

        StringBuilder builder = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            builder.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }

        return builder.toString();
    }
}
