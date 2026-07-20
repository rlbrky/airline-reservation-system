package com.berkay.airline_reservation_system;

import com.berkay.airline_reservation_system.model.Airport;
import com.berkay.airline_reservation_system.model.Flight;
import com.berkay.airline_reservation_system.repository.FlightRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class FlightRepositorySearchTest {

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void search_matchesRouteAndDay() {
        // given
        LocalDateTime date = LocalDateTime.of(2026, Month.JULY, 20, 14, 0);

        Airport istAirport = new Airport();
        istAirport.setCode("IST");
        istAirport.setName("Istanbul Airport");
        istAirport.setCity("Istanbul");
        entityManager.persist(istAirport);

        Airport lhrAirport = new Airport();
        lhrAirport.setCode("LHR");
        lhrAirport.setName("London Airport");
        lhrAirport.setCity("London");
        entityManager.persist(lhrAirport);

        Flight flight = new Flight();
        flight.setFlightNumber("05");
        flight.setOrigin(istAirport);
        flight.setDestination(lhrAirport);
        flight.setDepartureTime(date);
        flight.setArrivalTime(LocalDateTime.of(2026, Month.JULY, 20, 16, 0));
        flight.setPrice(BigDecimal.valueOf(150.00));
        entityManager.persist(flight);

        // wrong route but right day -> should be excluded
        Flight decoyFlight1 = new Flight();
        decoyFlight1.setFlightNumber("06");
        decoyFlight1.setOrigin(lhrAirport);
        decoyFlight1.setDestination(istAirport);
        decoyFlight1.setDepartureTime(date);
        decoyFlight1.setArrivalTime(LocalDateTime.of(2026, Month.JULY, 20, 16, 0));
        decoyFlight1.setPrice(BigDecimal.valueOf(150.00));
        entityManager.persist(decoyFlight1);

        // right route but wrong day -> should be excluded
        Flight decoyFlight2 = new Flight();
        decoyFlight2.setFlightNumber("07");
        decoyFlight2.setOrigin(istAirport);
        decoyFlight2.setDestination(lhrAirport);
        decoyFlight2.setDepartureTime(LocalDateTime.of(2026, Month.JULY, 22, 14, 0));
        decoyFlight2.setArrivalTime(LocalDateTime.of(2026, Month.JULY, 22, 17, 30));
        decoyFlight2.setPrice(BigDecimal.valueOf(250.00));
        entityManager.persist(decoyFlight2);

        // when

        // then
        List<Flight> result = flightRepository.search("IST", "LHR", date, LocalDateTime.of(2026, Month.JULY, 21, 14, 0));
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFlightNumber()).isEqualTo("05");
    }

}
