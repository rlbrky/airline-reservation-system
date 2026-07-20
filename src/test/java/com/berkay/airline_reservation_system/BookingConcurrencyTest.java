package com.berkay.airline_reservation_system;


import com.berkay.airline_reservation_system.model.*;
import com.berkay.airline_reservation_system.repository.*;
import com.berkay.airline_reservation_system.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test") // H2
public class BookingConcurrencyTest {

    @Autowired
    BookingService bookingService;

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    SeatRepository seatRepository;

    @Autowired
    FlightRepository flightRepository;

    @Autowired
    AirportRepository airportRepository;

    @Autowired
    UserRepository userRepository;

    Long seatId;
    Seat seat;

    @BeforeEach
    void setup() {
        bookingRepository.deleteAll();
        flightRepository.deleteAll();
        airportRepository.deleteAll();
        userRepository.deleteAll();
        seatRepository.deleteAll();

        AirlineUser user = new AirlineUser();
        user.setUsername("user");
        user.setPasswordHash("x");
        user.setFullName("Test User");
        user.setRole(Role.USER);
        userRepository.save(user);

        Airport ist = new Airport(); ist.setCode("IST"); ist.setName("Istanbul"); ist.setCity("Istanbul");
        Airport lhr = new Airport(); lhr.setCode("LHR"); lhr.setName("London");   lhr.setCity("London");
        airportRepository.save(ist);
        airportRepository.save(lhr);

        Flight flight = new Flight();
        flight.setFlightNumber("TEST1");
        flight.setOrigin(ist);
        flight.setDestination(lhr);
        flight.setDepartureTime(LocalDateTime.now().plusDays(1));
        flight.setArrivalTime(LocalDateTime.now().plusDays(1).plusHours(3));
        flight.setPrice(new BigDecimal("100.00"));
        flightRepository.save(flight);

        seat = new Seat();
        seat.setSeatNumber("1A");
        seat.setSeatClass(SeatClass.BUSINESS);
        seat.setSeatStatus(SeatStatus.AVAILABLE);
        seat.setFlight(flight);
        seatId = seatRepository.save(seat).getId(); // let db assign random id
    }

    @Test
    void onlyOneBookingWinsForTheSameSeat() throws Exception {

        int threads = 10;
        var pool = Executors.newFixedThreadPool(threads);
        var startLine = new CountDownLatch(1); // release all at once
        var done = new CountDownLatch(threads);
        var success = new AtomicInteger();

        for (int i = 0; i < threads; i++) {
            pool.submit(() -> {
                try {
                    startLine.await(); // everyone waits
                    bookingService.book("user", seat.getId());
                    success.incrementAndGet();
                } catch (Exception ignored) {
                    // Don't care
                } finally {
                    done.countDown();
                }
            });
        }

        startLine.countDown(); // GO
        done.await(10, TimeUnit.SECONDS);
        pool.shutdown();

        // then - exactly one winner, one confirmed booking, seat is booked.
        assertThat(success.get()).isEqualTo(1);
        assertThat(bookingRepository.findAll()).hasSize(1);
        assertThat(seatRepository.findById(seatId).get().getSeatStatus())
                .isEqualTo(SeatStatus.BOOKED);
    }
}
