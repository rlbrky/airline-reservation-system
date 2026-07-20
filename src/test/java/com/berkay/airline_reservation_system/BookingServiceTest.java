package com.berkay.airline_reservation_system;

import com.berkay.airline_reservation_system.exception.SeatUnavailableException;
import com.berkay.airline_reservation_system.model.*;
import com.berkay.airline_reservation_system.repository.BookingRepository;
import com.berkay.airline_reservation_system.repository.SeatRepository;
import com.berkay.airline_reservation_system.service.BookingService;
import com.berkay.airline_reservation_system.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private BookingService bookingService;

    @Test
    void book_marksSeatBookedAndCreatesBooking(){

        // given
        AirlineUser airlineUser = new AirlineUser();
        airlineUser.setUsername("test");

        Flight flight = new Flight();
        flight.setPrice(new BigDecimal("100.00"));

        Seat seat = new Seat();
        seat.setSeatStatus(SeatStatus.AVAILABLE);
        seat.setFlight(flight);

        Long seatId = 550L;
        when(userService.getByUsername("test")).thenReturn(airlineUser);
        when(seatRepository.findById(seatId)).thenReturn(Optional.of(seat));

        // when
        Booking booking = bookingService.book("test", seatId);

        // then
        assertThat(seat.getSeatStatus()).isEqualTo(SeatStatus.BOOKED);
        assertThat(booking.getBookingStatus()).isEqualTo(BookingStatus.CONFIRMED);
        assertThat(booking.getBookingReference()).isNotNull();
        assertThat(booking.getPricePaid()).isEqualByComparingTo(BigDecimal.valueOf(100.00));
        verify(bookingRepository).save(booking);
    }

    @Test
    void book_rejectsAlreadyBookedSeat() {
        // given
        AirlineUser airlineUser = new AirlineUser();
        airlineUser.setUsername("test");

        Seat seat = new Seat();
        seat.setSeatStatus(SeatStatus.BOOKED);

        Long seatId = 550L;
        when(userService.getByUsername("test")).thenReturn(airlineUser);
        when(seatRepository.findById(seatId)).thenReturn(Optional.of(seat));

        // then
        assertThatThrownBy(() -> bookingService.book("test", seatId))
                .isInstanceOf(SeatUnavailableException.class);
    }

    @Test
    void book_rejectsUnknownSeat() {
        AirlineUser user =  new AirlineUser();
        user.setUsername("test");

        when(userService.getByUsername("test")).thenReturn(user);
        when(seatRepository.findById(550L)).thenReturn(Optional.empty()); // seat not found

        assertThatThrownBy(() -> bookingService.book("test", 550L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void cancel_rejectsOthersBooking() {
        // given
        AirlineUser airlineUser = new AirlineUser();
        airlineUser.setUsername("test");

        Seat seat = new Seat();
        seat.setSeatStatus(SeatStatus.BOOKED);

        Booking booking = new Booking();
        booking.setBookingReference("ABC123");
        booking.setUser(airlineUser);
        booking.setSeat(seat);
        booking.setBookingStatus(BookingStatus.CONFIRMED);

        when(bookingRepository.findByBookingReference("ABC123")).thenReturn(Optional.of(booking));

        // when

        // then
        assertThatThrownBy(() -> bookingService.cancel("ABC123", "otherUser"))
                .isInstanceOf(IllegalArgumentException.class);
        // Seat untouched
        assertThat(seat.getSeatStatus()).isEqualTo(SeatStatus.BOOKED);
    }

    @Test
    void cancel_rejectsAlreadyCancelled() {
        // given
        AirlineUser airlineUser = new AirlineUser();
        airlineUser.setUsername("test");

        Booking booking = new Booking();
        booking.setBookingReference("ABC123");
        booking.setUser(airlineUser);
        booking.setBookingStatus(BookingStatus.CANCELED); // already cancelled booking

        when(bookingRepository.findByBookingReference("ABC123")).thenReturn(Optional.of(booking));

        // when

        // then
        assertThatThrownBy(() -> bookingService.cancel("ABC123", "test"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void cancel_freesSeatAndMarksCancelled() {
        // given
        AirlineUser airlineUser = new AirlineUser();
        airlineUser.setUsername("test");

        Seat seat = new Seat();
        seat.setSeatStatus(SeatStatus.BOOKED);

        Booking booking = new Booking();
        booking.setBookingReference("ABC123");
        booking.setUser(airlineUser);
        booking.setSeat(seat);
        booking.setBookingStatus(BookingStatus.CONFIRMED);

        when(bookingRepository.findByBookingReference("ABC123")).thenReturn(Optional.of(booking));

        // when
        bookingService.cancel("ABC123", "test");

        // then
        assertThat(booking.getBookingStatus()).isEqualTo(BookingStatus.CANCELED);
        assertThat(seat.getSeatStatus()).isEqualTo(SeatStatus.AVAILABLE);
    }
}
