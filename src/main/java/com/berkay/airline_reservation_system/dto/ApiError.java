package com.berkay.airline_reservation_system.dto;

import java.time.Instant;

public record ApiError(Instant timestamp, int status, String error, String message) {


}
