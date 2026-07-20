package com.berkay.airline_reservation_system;

import com.berkay.airline_reservation_system.model.Flight;
import com.berkay.airline_reservation_system.model.Seat;
import com.berkay.airline_reservation_system.model.SeatStatus;
import com.berkay.airline_reservation_system.repository.SeatRepository;
import com.berkay.airline_reservation_system.service.SeatService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SeatServiceTest {

    @Mock
    private SeatRepository seatRepository;

    @InjectMocks
    private SeatService seatService;

    @Test
    void generateSeats_createsRowsTimesColumns() {

        // given
        Flight flight = new Flight();
        seatService.generateSeats(flight, 10);

        // when
        ArgumentCaptor<List<Seat>> captor = ArgumentCaptor.forClass(List.class);
        verify(seatRepository).saveAll(captor.capture());
        List<Seat> seats = captor.getValue();

        // then
        assertThat(seats).hasSize(60); // 10 rows 6 columns = 60 seats
        assertThat(seats).allMatch(seat -> seat.getSeatStatus() == SeatStatus.AVAILABLE);
        // Ensure correct names are generated.
        assertThat(seats).extracting(Seat::getSeatNumber).contains("1A", "1F", "10A", "10F");
    }
}
