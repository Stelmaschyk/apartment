package util;

import com.apartment.apartment.dto.accommodation.AccommodationRequestDto;
import com.apartment.apartment.dto.booking.BookingRequestDto;
import com.apartment.apartment.dto.booking.BookingResponseDto;
import com.apartment.apartment.dto.booking.BookingSearchParametersDto;
import com.apartment.apartment.dto.payment.CancelPaymentResponseDto;
import com.apartment.apartment.dto.payment.PaymentRequestDto;
import com.apartment.apartment.dto.payment.PaymentResponseDto;
import com.apartment.apartment.dto.user.UpdateRoleRequestDto;
import com.apartment.apartment.dto.user.UserLoginRequestDto;
import com.apartment.apartment.dto.user.UserRegistrationRequestDto;
import com.apartment.apartment.dto.user.UserResponseDto;
import com.apartment.apartment.dto.user.UserRoleResponseDto;
import com.apartment.apartment.dto.user.UserUpdateRequestDto;
import com.apartment.apartment.model.Accommodation;
import com.apartment.apartment.model.Address;
import com.apartment.apartment.model.Booking;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public final class TestControllerDataProvider {
    public static AccommodationRequestDto createValidAccommodationRequestDto() {
        Address address = new Address()
                .setCountry("Ukraine")
                .setCity("Kyiv")
                .setStreet("McCaine")
                .setBuildNumber(7);

        List<String> amenities = Arrays.asList("Wi-Fi", "Parking", "Pool");

        return new AccommodationRequestDto()
            .setDailyRate(BigDecimal.valueOf(100.0))
            .setAvailability(5)
            .setType(Accommodation.AccommodationType.HOUSE)
            .setSize("Large")
            .setAddress(address)
            .setAmenities(amenities);
    }

    public static AccommodationRequestDto updatedValidAccommodationRequestDto(Long id) {
        Address address = new Address()
                .setCountry("Ukraine")
                .setCity("Kyiv")
                .setStreet("Kovalevskaya")
                .setBuildNumber(8);

        List<String> amenities = Arrays.asList("Wi-Fi", "Parking", "Run Place");

        return new AccommodationRequestDto()
            .setDailyRate(BigDecimal.valueOf(100.0))
            .setAvailability(5)
            .setType(Accommodation.AccommodationType.HOUSE)
            .setSize("Large")
            .setAddress(address)
            .setAmenities(amenities);
    }

    public static UserRegistrationRequestDto createValidRegistrationRequestDto() {
        return new UserRegistrationRequestDto()
            .setEmail("ivan.petrenko@gmail.com")
            .setPassword("password123")
            .setRepeatPassword("password123")
            .setFirstName("Ivan")
            .setLastName("Petrenko");
    }

    public static UserLoginRequestDto createValidUserLoginRequestDto() {
        return new UserLoginRequestDto()
            .setEmail("ivan.petrenko@gmail.com")
            .setPassword("password123");
    }

    public static UpdateRoleRequestDto createValidUpdateRoleRequestDto() {
        Set<Long> roleIds = Set.of(3L);
        return new UpdateRoleRequestDto(roleIds);
    }

    public static UserRoleResponseDto createValidUserRolesResponseDto() {
        Set<Long> roleIds = Set.of(3L);
        return new UserRoleResponseDto("ivan.petrenko@gmail.com", roleIds);
    }

    public static UserResponseDto createValidUserResponseDto() {
        return new UserResponseDto()
            .setId(1L)
            .setFirstName("Ivan")
            .setLastName("Petrenko")
            .setEmail("ivan.petrenko@gmail.com");
    }

    public static UserUpdateRequestDto createValidUserUpdateRequestDto() {
        return new UserUpdateRequestDto()
            .setFirstName("Monte")
            .setLastName("Christs");
    }

    public static BookingRequestDto createValidBookingRequestDto() {
        return new BookingRequestDto()
            .setCheckInDate(LocalDate.of(2023, 10, 1))
            .setCheckOutDate(LocalDate.of(2023, 10, 5))
            .setAccommodationId(1L);
    }

    public static BookingRequestDto createValidUpdatedBookingRequestDto() {
        return new BookingRequestDto()
            .setCheckInDate(LocalDate.of(2025, 1, 1))
            .setCheckOutDate(LocalDate.of(2025, 1, 5))
            .setAccommodationId(2L);
    }

    public static BookingResponseDto createUpdatedBookingResponseDto() {
        return new BookingResponseDto()
            .setBookingId(1L)
            .setUserId(1L)
            .setCheckInDate(LocalDate.of(2025, 1, 1))
            .setCheckOutDate(LocalDate.of(2025, 1, 5))
            .setAccommodationId(2L)
            .setStatus(Booking.BookingStatus.PENDING);
    }

    public static BookingResponseDto createValidBookingResponseDto() {
        return new BookingResponseDto()
            .setBookingId(1L)
            .setUserId(1L)
            .setCheckInDate(LocalDate.of(2023, 10, 1))
            .setCheckOutDate(LocalDate.of(2023, 10, 5))
            .setAccommodationId(1L)
            .setStatus(Booking.BookingStatus.PENDING);
    }

    public static BookingSearchParametersDto createValidBookingSearchParametersDto() {
        String[] userIds = {"1", "2"};
        String[] statuses = {"CONFIRMED", "PENDING"};
        return new BookingSearchParametersDto(userIds, statuses);
    }

    public static PaymentRequestDto createValidPaymentRequestDto() {
        return new PaymentRequestDto(1L);
    }

    public static PaymentResponseDto createValidPaymentResponseDto() {
        return new PaymentResponseDto(
            1L,
            "PENDING",
            BigDecimal.valueOf(1000.00),
            "https://checkout.stripe.com/c/pay/cs_test_a1QpOwJuVdsnxVDllwnHk82"
        );
    }

    public static CancelPaymentResponseDto createValidCancelPaymentResponseDto() {
        return new CancelPaymentResponseDto(
            1L,
            "CANCELED",
            BigDecimal.valueOf(1000.00)
        );
    }
}
