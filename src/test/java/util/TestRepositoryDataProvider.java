package util;

import com.apartment.apartment.model.Accommodation;
import com.apartment.apartment.model.Address;
import com.apartment.apartment.model.Booking;
import com.apartment.apartment.model.Role;
import com.apartment.apartment.model.User;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestRepositoryDataProvider {
    public static User createValidUser() {
        Set<Role> roles = new HashSet<>();
        Role role = new Role();
        role.setId(2L);
        role.setRole(Role.RoleName.ROLE_USER);
        roles.add(role);

        return new User()
            .setFirstName("John")
            .setLastName("Stelm")
            .setEmail("john.stelm@gmail.com")
            .setPassword("password123")
            .setRoles(roles);
    }

    public static Accommodation createValidAccommodation() {
        Address address = new Address()
                .setCountry("Ukraine")
                .setCity("Kyiv")
                .setStreet("Khreshchatyk")
                .setBuildNumber(25);

        List<String> amenities = Arrays.asList("Wi-Fi", "Parking", "Run Place");

        return new Accommodation()
            .setDailyRate(new BigDecimal("200.00"))
            .setAvailability(30)
            .setAccommodationType(Accommodation.AccommodationType.GUESTHOUSE)
            .setSize("small")
            .setAddress(address)
            .setAmenities(amenities);
    }

    public static Booking createValidBooking() {
        User user = new User()
                .setId(1L)
                .setFirstName("John")
                .setLastName("Doe")
                .setEmail("john.doe@gmail.com")
                .setPassword("password123")
                .setRoles(new HashSet<>());

        Accommodation accommodation = new Accommodation()
                .setId(1L)
                .setDailyRate(new BigDecimal("200.00"))
                .setAvailability(30)
                .setAccommodationType(Accommodation.AccommodationType.GUESTHOUSE)
                .setSize("small")
                .setAddress(new Address().setCountry("Ukraine").setCity("Kyiv").setStreet(
                    "Khreshchatyk").setBuildNumber(25))
                .setAmenities(new ArrayList<>(Arrays.asList("wifi", "air condition", "kitchen")));

        return new Booking()
            .setUser(user)
            .setCheckInDate(LocalDate.of(2025, 2, 10))
            .setCheckOutDate(LocalDate.of(2025, 2, 15))
            .setAccommodation(accommodation)
            .setStatus(Booking.BookingStatus.PENDING);
    }
}
