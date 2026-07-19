package com.berkay.airline_reservation_system.dto;

import jakarta.validation.constraints.NotNull;

public record BookingRequest(@NotNull Long seatId) {

    // Used as POST body
}
