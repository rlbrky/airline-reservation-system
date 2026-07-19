package com.berkay.airline_reservation_system.config;

import com.berkay.airline_reservation_system.model.AirlineUser;
import com.berkay.airline_reservation_system.model.Airport;
import com.berkay.airline_reservation_system.model.Flight;
import com.berkay.airline_reservation_system.model.Role;
import com.berkay.airline_reservation_system.repository.AirportRepository;
import com.berkay.airline_reservation_system.repository.FlightRepository;
import com.berkay.airline_reservation_system.repository.SeatRepository;
import com.berkay.airline_reservation_system.repository.UserRepository;
import com.berkay.airline_reservation_system.service.SeatService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;

@Profile("dev")
@Component
public class DataSeeder implements CommandLineRunner {

    private final AirportRepository airportRepository;

    private final FlightRepository flightRepository;

    private final SeatRepository seatRepository;

    private final SeatService seatService;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public DataSeeder(AirportRepository airportRepository, FlightRepository flightRepository, SeatRepository seatRepository, SeatService seatService, UserRepository userRepository) {
        this.airportRepository = airportRepository;
        this.flightRepository = flightRepository;
        this.seatRepository = seatRepository;
        this.seatService = seatService;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        // Generate airports
        if(airportRepository.count() == 0) {
            Airport airport1 = new Airport();
            airport1.setCity("Istanbul");
            airport1.setName("Istanbul");
            airport1.setCode("IST");
            airportRepository.save(airport1);

            Airport airport2 = new Airport();
            airport2.setCity("Istanbul");
            airport2.setName("Sabiha Gökçen");
            airport2.setCode("SAW");
            airportRepository.save(airport2);

            Airport airport3 = new Airport();
            airport3.setCity("London");
            airport3.setName("London Heathrow");
            airport3.setCode("LHR");
            airportRepository.save(airport3);

            Airport airport4 = new Airport();
            airport4.setCity("New York");
            airport4.setName("New York");
            airport4.setCode("JFK");
            airportRepository.save(airport4);

            Airport airport5 = new Airport();
            airport5.setCity("Paris");
            airport5.setName("Paris");
            airport5.setCode("CDG");
            airportRepository.save(airport5);
        }

        // Generate flights
        if(flightRepository.count() == 0){
            Flight flight1 = new Flight();
            flight1.setFlightNumber("101");
            flight1.setOrigin(airportRepository.findByCode("IST").get());
            flight1.setDestination(airportRepository.findByCode("LHR").get());
            flight1.setDepartureTime(LocalDateTime.of(2026, Month.JULY, 18, 14, 0));
            flight1.setArrivalTime(LocalDateTime.of(2026, Month.JULY, 18, 18, 0));
            flight1.setPrice(BigDecimal.valueOf(150));
            flightRepository.save(flight1);

            Flight flight2 = new Flight();
            flight2.setFlightNumber("102");
            flight2.setOrigin(airportRepository.findByCode("SAW").get());
            flight2.setDestination(airportRepository.findByCode("JFK").get());
            flight2.setDepartureTime(LocalDateTime.of(2026, Month.JULY, 18, 15, 0));
            flight2.setArrivalTime(LocalDateTime.of(2026, Month.JULY, 18, 21, 0));
            flight2.setPrice(BigDecimal.valueOf(250));
            flightRepository.save(flight2);

            Flight flight3 = new Flight();
            flight3.setFlightNumber("103");
            flight3.setOrigin(airportRepository.findByCode("SAW").get());
            flight3.setDestination(airportRepository.findByCode("CDG").get());
            flight3.setDepartureTime(LocalDateTime.of(2026, Month.JULY, 18, 12, 0));
            flight3.setArrivalTime(LocalDateTime.of(2026, Month.JULY, 18, 18, 0));
            flight3.setPrice(BigDecimal.valueOf(300));
            flightRepository.save(flight3);

            Flight flight4 = new Flight();
            flight4.setFlightNumber("201");
            flight4.setOrigin(airportRepository.findByCode("CDG").get());
            flight4.setDestination(airportRepository.findByCode("LHR").get());
            flight4.setDepartureTime(LocalDateTime.of(2026, Month.JULY, 20, 10, 0));
            flight4.setArrivalTime(LocalDateTime.of(2026, Month.JULY, 20, 15, 30));
            flight4.setPrice(BigDecimal.valueOf(200));
            flightRepository.save(flight4);

            Flight flight5 = new Flight();
            flight5.setFlightNumber("202");
            flight5.setOrigin(airportRepository.findByCode("CDG").get());
            flight5.setDestination(airportRepository.findByCode("JFK").get());
            flight5.setDepartureTime(LocalDateTime.of(2026, Month.JULY, 21, 12, 0));
            flight5.setArrivalTime(LocalDateTime.of(2026, Month.JULY, 21, 16, 30));
            flight5.setPrice(BigDecimal.valueOf(250));
            flightRepository.save(flight5);

            Flight flight6 = new Flight();
            flight6.setFlightNumber("203");
            flight6.setOrigin(airportRepository.findByCode("CDG").get());
            flight6.setDestination(airportRepository.findByCode("SAW").get());
            flight6.setDepartureTime(LocalDateTime.of(2026, Month.JULY, 16, 9, 0));
            flight6.setArrivalTime(LocalDateTime.of(2026, Month.JULY, 16, 14, 15));
            flight6.setPrice(BigDecimal.valueOf(175));
            flightRepository.save(flight6);

            Flight flight7 = new Flight();
            flight7.setFlightNumber("301");
            flight7.setOrigin(airportRepository.findByCode("JFK").get());
            flight7.setDestination(airportRepository.findByCode("LHR").get());
            flight7.setDepartureTime(LocalDateTime.of(2026, Month.JULY, 20, 8, 30));
            flight7.setArrivalTime(LocalDateTime.of(2026, Month.JULY, 20, 12, 15));
            flight7.setPrice(BigDecimal.valueOf(275));
            flightRepository.save(flight7);

            Flight flight8 = new Flight();
            flight8.setFlightNumber("302");
            flight8.setOrigin(airportRepository.findByCode("JFK").get());
            flight8.setDestination(airportRepository.findByCode("SAW").get());
            flight8.setDepartureTime(LocalDateTime.of(2026, Month.JULY, 22, 12, 15));
            flight8.setArrivalTime(LocalDateTime.of(2026, Month.JULY, 22, 21, 30));
            flight8.setPrice(BigDecimal.valueOf(350));
            flightRepository.save(flight8);

            Flight flight9 = new Flight();
            flight9.setFlightNumber("303");
            flight9.setOrigin(airportRepository.findByCode("JFK").get());
            flight9.setDestination(airportRepository.findByCode("CDG").get());
            flight9.setDepartureTime(LocalDateTime.of(2026, Month.JULY, 21, 14, 15));
            flight9.setArrivalTime(LocalDateTime.of(2026, Month.JULY, 21, 19, 45));
            flight9.setPrice(BigDecimal.valueOf(250));
            flightRepository.save(flight9);
        }

        // Generate seats for flights that lack seats.
        for (Flight f : flightRepository.findAll()) {
            if (seatRepository.findByFlight(f).isEmpty()) {
                seatService.generateSeats(f, 10);
            }
        }

        if (userRepository.findByUsername("admin").isEmpty()) {
            AirlineUser admin = new AirlineUser();
            admin.setUsername("admin");
            admin.setPasswordHash(passwordEncoder.encode("admin"));   // dev only
            admin.setFullName("Site Admin");
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
        }
    }
}
