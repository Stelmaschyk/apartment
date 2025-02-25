package repository;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.apartment.apartment.ApartmentApplication;
import com.apartment.apartment.model.Booking;
import com.apartment.apartment.repository.booking.BookingRepository;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

@SpringBootTest(classes = ApartmentApplication.class)
public class BookingRepositoryTest {
    private static final Long TEST_VALID_BOOKING_ID = 1L;
    private static final Long INCORRECT_BOOKING_ID = 80L;
    private static final Long TEST_USER_ID = 1L;

    @Autowired
    private BookingRepository bookingRepository;

    @BeforeAll
    public static void beforeAll(@Autowired DataSource dataSource) throws SQLException {
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection, new ClassPathResource(
                    "databases/bookings/repository/add-booking-related-tables.sql"));
        }
    }

    @AfterAll
    public static void afterAll(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @SneakyThrows
    public static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection, new ClassPathResource(
                    "databases/delete-data-from-all-tables.sql"));
        }
    }

    @Test
    @DisplayName("findByIdAndUserIdAndStatus with valid booking should return booking")
    public void testFindByIdAndUserIdAndStatus_ValidBooking() {
        Optional<Booking> booking =
                bookingRepository.findByIdAndUserIdAndStatus(TEST_VALID_BOOKING_ID, TEST_USER_ID,
                Booking.BookingStatus.PENDING);

        assertThat(booking).isPresent();
        assertThat(booking.get().getId()).isEqualTo(TEST_VALID_BOOKING_ID);
        assertThat(booking.get().getUser().getId()).isEqualTo(TEST_USER_ID);
        assertThat(booking.get().getStatus()).isEqualTo(Booking.BookingStatus.PENDING);
    }

    @Test
    @DisplayName("findByIdAndUserIdAndStatus with invalid booking should return empty")
    public void findByIdAndUserIdAndStatus_InvalidBooking() {
        Optional<Booking> booking =
                bookingRepository.findByIdAndUserIdAndStatus(INCORRECT_BOOKING_ID, TEST_USER_ID,
                Booking.BookingStatus.PENDING);
        assertThat(booking).isNotPresent();
    }

    @Test
    @DisplayName("findAllByStatusNotInAndCheckOutDate with no matching bookings should return "
            + "empty list")
    public void findAllByStatusNotInAndCheckOutDate_EmptyResult() {
        List<Booking> bookings = bookingRepository.findAllByStatusNotInAndCheckOutDate(
                Arrays.asList(Booking.BookingStatus.CONFIRMED),
                LocalDate.of(2025, 12, 31)
        );
        assertThat(bookings).isEmpty();
    }
}
