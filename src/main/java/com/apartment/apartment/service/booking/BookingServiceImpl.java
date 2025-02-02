package com.apartment.apartment.service.booking;

import static com.apartment.apartment.model.Booking.BookingStatus.CANCELED;
import static com.apartment.apartment.model.Booking.BookingStatus.CONFIRMED;

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
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;
    private final AccommodationRepository accommodationRepository;
    private final BookingSpecificationBuilder bookingSpecificationBuilder;

    @Override
    public BookingResponseDto save(Long id, BookingRequestDto requestDto) {
        Booking booking = bookingMapper.toModel(requestDto);
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                "can't find user by id: %d".formatted(id)));
        Accommodation accommodation = getAccommodationById(requestDto.getAccommodationId());
        booking.setAccommodation(accommodation);
        booking.setUser(user);
        booking.setStatus(Booking.BookingStatus.PENDING);
        Booking savedBooking = bookingRepository.save(booking);
        return bookingMapper.toDto(savedBooking);
    }

    @Override
    public List<BookingResponseDto> findAllByUserId(Long userId) {
        return bookingRepository.findAll().stream()
            .filter(booking -> booking.getUser().getId().equals(userId))
            .map(bookingMapper::toDto)
            .toList();
    }

    @Override
    public BookingResponseDto findById(Long id) {
        Booking booking = getBookingById(id);
        return bookingMapper.toDto(booking);
    }

    @Override
    public void update(Long id, BookingRequestDto bookingRequestDto) {
        Booking booking = getBookingById(id);
        bookingMapper.updateDto(booking, bookingRequestDto);
        Accommodation accommodation = getAccommodationById(bookingRequestDto.getAccommodationId());
        booking.setAccommodation(accommodation);
        bookingRepository.save(booking);
    }

    @Override
    public List<BookingResponseDto> search(BookingSearchParametersDto params,
                                           Pageable pageable) {
        Specification<Booking> bookingSpecification = bookingSpecificationBuilder.build(params);
        return bookingRepository.findAll(bookingSpecification, pageable).getContent().stream()
            .map(bookingMapper::toDto)
            .toList();
    }

    @Override
    public void deleteById(Long id) {
        bookingRepository.deleteById(id);
    }

    private Accommodation getAccommodationById(Long id) {
        return accommodationRepository.findById(id).orElseThrow(
            () -> new EntityNotFoundException("can't find accommodation by id: %d".formatted(id)));
    }

    @Override
    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id).orElseThrow(
            () -> new EntityNotFoundException("can't find booking by id: %d".formatted(id)));
    }

    @Override
    public List<Booking> expiredBookings() {
        List<Booking> expiredBookingList = bookingRepository.findAllByStatusNotInAndCheckOutDate(
                List.of(CANCELED, CONFIRMED), LocalDate.now().plusDays(1L));
        if (!expiredBookingList.isEmpty()) {
            expiredBookingList.forEach(booking -> booking.setStatus(Booking.BookingStatus.EXPIRED));
            return bookingRepository.saveAll(expiredBookingList);
        }
        return List.of();
    }
}
