package com.berkay.airline_reservation_system.service;

import com.berkay.airline_reservation_system.model.Flight;
import com.berkay.airline_reservation_system.model.Seat;
import com.berkay.airline_reservation_system.model.SeatClass;
import com.berkay.airline_reservation_system.model.SeatStatus;
import com.berkay.airline_reservation_system.repository.SeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class SeatService {

    private final SeatRepository seatRepository;

    public SeatService(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    @Transactional
    public void generateSeats(Flight flight, int rows) {

        List<Seat> seats = new ArrayList<>();

        for ( int i = 1; i <= rows; i++ ) {
            for ( char letter = 'A'; letter <= 'F'; letter++ ) {
                Seat seat = new Seat();
                // Formatted as: 12A, 5B etc.
                seat.setSeatNumber(String.valueOf(i) + letter);

                // Hardcoded business class seat generation.
                if(i <= 2) {
                    seat.setSeatClass(SeatClass.BUSINESS);
                }
                else {
                    seat.setSeatClass(SeatClass.ECONOMY);
                }

                seat.setSeatStatus(SeatStatus.AVAILABLE);
                seat.setFlight(flight);

                seats.add(seat);
            }
        }

        seatRepository.saveAll(seats);
    }

    @Transactional(readOnly = true)
    public List<Seat> getSeatsByFlightOrderByIdAsc(Flight flight) {
        return seatRepository.findByFlightOrderByIdAsc(flight);
    }

    @Transactional(readOnly = true)
    public long countByFlightAndSeatStatus(Flight flight, SeatStatus seatStatus) {

        List<Seat> seats = seatRepository.findByFlight(flight);
        long seatCountByStatus = 0;

        for (Seat seat : seats) {
            if(seat.getSeatStatus().equals(seatStatus)) {
                seatCountByStatus++;
            }
        }

        return seatCountByStatus;
    }
}
