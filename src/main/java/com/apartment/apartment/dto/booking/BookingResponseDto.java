package com.apartment.apartment.dto.booking;

import com.apartment.apartment.model.Booking;
import java.time.LocalDate;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class BookingResponseDto {
    private Long bookingId;
    private Long userId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Long accommodationId;
    private Booking.BookingStatus status;
}
