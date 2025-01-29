package com.apartment.apartment.controller;

import com.apartment.apartment.dto.booking.BookingRequestDto;
import com.apartment.apartment.dto.booking.BookingResponseDto;
import com.apartment.apartment.dto.booking.BookingSearchParametersDto;
import com.apartment.apartment.model.User;
import com.apartment.apartment.service.booking.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Bookings management",
        description = "Endpoints for bookings managing.")
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create booking after authentication")
    public BookingResponseDto createBooking(@AuthenticationPrincipal User user,
                                            @RequestBody BookingRequestDto requestDto) {
        return bookingService.save(user.getId(), requestDto);
    }

    @GetMapping("/me")
    @Operation(summary = "Get all booking based on authentication token")
    public List<BookingResponseDto> getBookings(@AuthenticationPrincipal User user) {
        return bookingService.findAllByUserId(user.getId());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get booking by id")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponseDto getBookingById(@PathVariable Long id) {
        return bookingService.findById(id);
    }

    @Operation(summary = "Update booking`s information", description = "Update booking`s "
            + "information by Id")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    void updateBooking(@PathVariable Long id, @RequestBody BookingRequestDto requestDto) {
        bookingService.update(id, requestDto);
    }

    @Operation(summary = "Search bookings by userId and status parameters only")
    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingResponseDto> searchBook(BookingSearchParametersDto searchParameters,
                                               Pageable pageable) {
        return bookingService.search(searchParameters, pageable);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBookingById(@PathVariable Long id) {
        bookingService.deleteById(id);
    }
}
