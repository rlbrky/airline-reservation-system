package com.berkay.airline_reservation_system.repository;

import com.berkay.airline_reservation_system.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findByBookingReference(String reference);
}
