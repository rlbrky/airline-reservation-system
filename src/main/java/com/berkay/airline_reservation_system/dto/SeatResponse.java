package com.berkay.airline_reservation_system.dto;

import com.berkay.airline_reservation_system.model.Seat;
import com.berkay.airline_reservation_system.model.SeatClass;
import com.berkay.airline_reservation_system.model.SeatStatus;

public record SeatResponse(Long id, String seatNumber, SeatClass seatClass, SeatStatus seatStatus) {

    public static SeatResponse from(Seat seat) {
        return new SeatResponse(seat.getId(), seat.getSeatNumber(), seat.getSeatClass(), seat.getSeatStatus());
    }
}
