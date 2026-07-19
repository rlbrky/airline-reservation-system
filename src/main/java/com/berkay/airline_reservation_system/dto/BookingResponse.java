package com.berkay.airline_reservation_system.dto;

import com.berkay.airline_reservation_system.model.Booking;
import com.berkay.airline_reservation_system.model.BookingStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record BookingResponse(String reference, String flightNumber, String seatNumber,
                              BookingStatus bookingStatus, Instant bookedAt, BigDecimal pricePaid) {

    public static BookingResponse from(Booking booking) {
        return new BookingResponse(booking.getBookingReference(), booking.getFlight().getFlightNumber(), booking.getSeat().getSeatNumber(),
                booking.getBookingStatus(), booking.getBookedAt(), booking.getPricePaid());
    }
}
