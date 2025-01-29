package com.apartment.apartment.service.booking;

import com.apartment.apartment.dto.booking.BookingRequestDto;
import com.apartment.apartment.dto.booking.BookingResponseDto;
import com.apartment.apartment.dto.booking.BookingSearchParametersDto;
import com.apartment.apartment.model.Booking;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface BookingService {
    BookingResponseDto save(Long id, BookingRequestDto bookingRequestDto);

    List<BookingResponseDto> findAllByUserId(Long userId);

    BookingResponseDto findById(Long id);

    void update(Long id, BookingRequestDto bookingRequestDto);

    List<BookingResponseDto> search(BookingSearchParametersDto bookSearchParameters,
                                    Pageable pageable);

    void deleteById(Long id);

    List<Booking> expiredBookings();
}
