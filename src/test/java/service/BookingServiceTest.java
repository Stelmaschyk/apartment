package service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.apartment.apartment.dto.booking.BookingRequestDto;
import com.apartment.apartment.dto.booking.BookingResponseDto;
import com.apartment.apartment.dto.booking.BookingSearchParametersDto;
import com.apartment.apartment.exception.EntityNotFoundException;
import com.apartment.apartment.mapper.BookingMapper;
import com.apartment.apartment.model.Accommodation;
import com.apartment.apartment.model.Booking;
import com.apartment.apartment.model.User;
import com.apartment.apartment.repository.accommodation.AccommodationRepository;
import com.apartment.apartment.repository.booking.BookingRepository;
import com.apartment.apartment.repository.booking.BookingSpecificationBuilder;
import com.apartment.apartment.repository.user.UserRepository;
import com.apartment.apartment.service.booking.BookingServiceImpl;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    private static final Long TEST_USER_ID = 1L;
    private static final Long TEST_BOOKING_ID = 1L;
    private static final Long TEST_ACCOMMODATION_ID = 1L;

    private static Booking booking;
    private static BookingResponseDto responseDto;
    private static BookingRequestDto requestDto;

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AccommodationRepository accommodationRepository;
    @Mock
    private BookingMapper bookingMapper;
    @Mock
    private BookingSpecificationBuilder bookingSpecificationBuilder;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @BeforeAll
    static void setup() {
        User user = new User();
        user.setId(TEST_USER_ID);

        Accommodation accommodation = new Accommodation()
                .setId(TEST_ACCOMMODATION_ID)
                .setDailyRate(BigDecimal.valueOf(100));

        booking = new Booking()
            .setId(TEST_BOOKING_ID)
            .setUser(user)
            .setCheckInDate(LocalDate.now())
            .setCheckOutDate(LocalDate.now().plusDays(5))
            .setAccommodation(accommodation)
            .setStatus(Booking.BookingStatus.PENDING);

        responseDto = new BookingResponseDto()
            .setBookingId(TEST_BOOKING_ID)
            .setUserId(TEST_USER_ID)
            .setCheckInDate(booking.getCheckInDate())
            .setCheckOutDate(booking.getCheckOutDate())
            .setAccommodationId(TEST_ACCOMMODATION_ID)
            .setStatus(Booking.BookingStatus.PENDING);

        requestDto = new BookingRequestDto()
            .setCheckInDate(LocalDate.now())
            .setCheckOutDate(LocalDate.now().plusDays(5))
            .setAccommodationId(TEST_ACCOMMODATION_ID);
    }

    @Test
    void saveBooking_shouldSaveWithReturningBookingResponseDto() {
        when(bookingMapper.toModel(requestDto)).thenReturn(booking);
        when(userRepository.findById(TEST_USER_ID))
                .thenReturn(Optional.of(new User().setId(TEST_USER_ID)));
        when(accommodationRepository.findById(TEST_ACCOMMODATION_ID))
                .thenReturn(Optional.of(new Accommodation().setId(TEST_ACCOMMODATION_ID)));
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toDto(booking)).thenReturn(responseDto);

        BookingResponseDto actual = bookingService.save(TEST_USER_ID, requestDto);

        assertThat(actual)
            .isNotNull()
                .isEqualTo(responseDto);

        verify(bookingRepository).save(booking);
    }

    @Test
    void findById_withValidId_shouldReturnBookingResponseDto() {
        when(bookingRepository.findById(TEST_BOOKING_ID)).thenReturn(Optional.of(booking));
        when(bookingMapper.toDto(booking)).thenReturn(responseDto);

        BookingResponseDto result = bookingService.findById(TEST_BOOKING_ID);

        assertThat(result).isEqualTo(responseDto);
        verify(bookingRepository).findById(TEST_BOOKING_ID);
    }

    @Test
    void findById_withInvalidId_shouldThrowException() {
        when(bookingRepository.findById(TEST_BOOKING_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.findById(TEST_BOOKING_ID));
        verify(bookingRepository).findById(TEST_BOOKING_ID);
    }

    @Test
    void searchBookings_withValidParams_shouldReturnListOfResponseDtos() {
        BookingSearchParametersDto searchParams =
                new BookingSearchParametersDto(new String[]{"1"}, new String[]{"CONFIRMED"});
        Specification<Booking> specification = mock(Specification.class);
        Pageable pageable = PageRequest.of(0, 10);
        List<Booking> bookingList = List.of(booking);
        Page<Booking> bookingPage = new PageImpl<>(bookingList);

        when(bookingSpecificationBuilder.build(searchParams)).thenReturn(specification);
        when(bookingRepository.findAll(specification, pageable)).thenReturn(bookingPage);
        when(bookingMapper.toDto(any(Booking.class))).thenReturn(responseDto);

        List<BookingResponseDto> result = bookingService.search(searchParams, pageable);

        assertThat(result)
            .isNotNull()
            .hasSize(1)
                .containsExactly(responseDto);

        verify(bookingSpecificationBuilder).build(searchParams);
        verify(bookingRepository).findAll(specification, pageable);
        verify(bookingMapper).toDto(booking);
    }
}
