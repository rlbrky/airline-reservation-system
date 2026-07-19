package com.berkay.airline_reservation_system.exception;

public class SeatUnavailableException extends RuntimeException {
    public SeatUnavailableException(String message) {

        super(message);
    }
}
