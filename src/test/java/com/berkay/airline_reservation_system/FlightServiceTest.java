package com.berkay.airline_reservation_system;

import com.berkay.airline_reservation_system.model.Airport;
import com.berkay.airline_reservation_system.model.Flight;
import com.berkay.airline_reservation_system.repository.AirportRepository;
import com.berkay.airline_reservation_system.repository.FlightRepository;
import com.berkay.airline_reservation_system.service.FlightService;
import com.berkay.airline_reservation_system.service.SeatService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FlightServiceTest {

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private AirportRepository airportRepository;

    @Mock
    private SeatService seatService;

    @InjectMocks
    private FlightService flightService;

    @Test
    void search_rejectsSameOriginAndDestination() {

        assertThatThrownBy(() -> flightService.search("ASD", "ASD", LocalDate.now()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void createFlight_rejectsUnknownAirport() {

        // given
        when(airportRepository.findByCode("test")).thenReturn(Optional.empty()); // to get origin not found

        LocalDateTime departure = LocalDateTime.of(2026, Month.JANUARY, 1, 9, 0, 0);
        LocalDateTime arrival = LocalDateTime.of(2026, Month.JANUARY, 1, 11, 0, 0);

        // then
        assertThatThrownBy(() -> flightService.createFlight(
                "101",
                "test",
                "testDest",
                departure,
                arrival,
                BigDecimal.valueOf(100.00),
                2))
        .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void createFlight_generatesSeats() {
        // given
        Airport origin = new Airport();
        origin.setCode("test");

        Airport destination = new Airport();
        destination.setCode("testDest");

        when(airportRepository.findByCode("test")).thenReturn(Optional.of(origin));
        when(airportRepository.findByCode("testDest")).thenReturn(Optional.of(destination));

        LocalDateTime departure = LocalDateTime.of(2026, Month.JANUARY, 1, 9, 0, 0);
        LocalDateTime arrival = LocalDateTime.of(2026, Month.JANUARY, 1, 11, 0, 0);

        // when
        Flight flight = flightService.createFlight(
                "101",
                "test",
                "testDest",
                departure,
                arrival,
                BigDecimal.valueOf(100.00),
                2);

        // then
        verify(seatService, times(1)).generateSeats(flight, 2);
        verify(flightRepository, times(1)).save(flight);
    }
}
