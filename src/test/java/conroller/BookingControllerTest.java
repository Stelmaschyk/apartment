package conroller;

import static com.apartment.apartment.model.Booking.BookingStatus.CONFIRMED;
import static com.apartment.apartment.model.Booking.BookingStatus.PENDING;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.apartment.apartment.ApartmentApplication;
import com.apartment.apartment.dto.booking.BookingRequestDto;
import com.apartment.apartment.dto.booking.BookingResponseDto;
import com.apartment.apartment.dto.booking.BookingSearchParametersDto;
import com.apartment.apartment.service.booking.BookingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import util.TestDataProvider;

@SpringBootTest(classes = ApartmentApplication.class)
public class BookingControllerTest {
    protected static MockMvc mockMvc;
    private static final Long TEST_USER_ID = 1L;
    private static final Long TEST_BOOKING_ID = 1L;
    private static final String TEST_USER_EMAIL = "user@gmail.com";
    private static final int EXPECTED_LENGTH = 3;
    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private BookingService bookingService;

    @BeforeAll
    static void beforeAll(@Autowired DataSource dataSource,
                          @Autowired WebApplicationContext applicationContext
    ) throws SQLException {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(applicationContext)
            .apply(springSecurity())
            .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                new ClassPathResource(
                    "databases/roles/add-roles.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                new ClassPathResource(
                    "databases/accommodations/add-accommodations-to-accommodations-table.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                new ClassPathResource(
                    "databases/user/add-user-to-tables-users.sql")
            );
        }
    }

    @AfterAll
    static void afterAll(
            @Autowired DataSource dataSource
    ) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                new ClassPathResource(
                    "databases/delete-data-from-all-tables.sql")
            );
        }
    }

    @WithUserDetails(value = TEST_USER_EMAIL)
    @Test
    @Sql(scripts = "classpath:databases/bookings/delete-from-bookings.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createBooking_validBookingRequestDto_success() throws Exception {
        BookingRequestDto requestDto = TestDataProvider.createValidBookingRequestDto();
        BookingResponseDto expected = TestDataProvider.createValidBookingResponseDto();

        when(bookingService.save(eq(TEST_USER_ID), any(BookingRequestDto.class)))
                .thenReturn(expected);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/bookings")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        BookingResponseDto actual =
                objectMapper.readValue(result.getResponse()
                .getContentAsString(), BookingResponseDto.class);

        assertThat(actual)
            .isNotNull()
            .usingRecursiveComparison()
                .ignoringFields("bookingId")
                .isEqualTo(expected);
    }

    @WithUserDetails(value = TEST_USER_EMAIL)
    @Test
    @Sql (scripts = "classpath:databases/bookings/add-booking-to-bookings-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql (scripts = "classpath:databases/bookings/delete-from-bookings.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getBookingById_validBookingRequestDto_success() throws Exception {
        MvcResult result = mockMvc.perform(get("/bookings/{id}", TEST_BOOKING_ID)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookingResponseDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), BookingResponseDto.class);

        assertThat(actual.getBookingId())
                .isNotNull()
                .isEqualTo(TEST_BOOKING_ID);
    }

    @WithUserDetails(value = TEST_USER_EMAIL)
    @Test
    @Sql (scripts = "classpath:databases/bookings/add-booking-to-bookings-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql (scripts = "classpath:databases/bookings/delete-from-bookings.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getBookingsList_validUserId_retrieveListOfBookingResponseDto() throws Exception {

        MvcResult result = mockMvc.perform(get("/bookings/me", TEST_USER_ID)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookingResponseDto[] actual = objectMapper.readValue(result.getResponse()
            .getContentAsString(), BookingResponseDto[].class);
        List<Long> bookingIds = Arrays.stream(actual)
                .map(BookingResponseDto::getBookingId)
                .toList();

        AssertionsForInterfaceTypes.assertThat(bookingIds)
                .hasSize(EXPECTED_LENGTH)
                .containsExactly(1L, 2L, 3L);
    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("search booking by parameters user_id or status")
    @Sql (scripts = "classpath:databases/bookings/add-booking-to-bookings-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql (scripts = "classpath:databases/bookings/delete-from-bookings.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void searchBookings_BookingSearchParametersDto_ReturnListBookingResponseDto() throws Exception {
        BookingSearchParametersDto params =
                TestDataProvider.createValidBookingSearchParametersDto();

        MvcResult result = mockMvc.perform(get("/bookings/search")
                .content(objectMapper.writeValueAsString(params))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookingResponseDto[] actual = objectMapper.readValue(
            result.getResponse().getContentAsString(), BookingResponseDto[].class);

        assertThat(actual)
                .isNotEmpty()
                .anySatisfy(bookingResponseDto -> assertThat(bookingResponseDto.getUserId())
                .isIn(1L, 2L))
                .anySatisfy(bookingResponseDto -> assertThat(bookingResponseDto.getStatus())
                .isIn(CONFIRMED, PENDING));
    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("update booking by booking_id")
    @Sql (scripts = "classpath:databases/bookings/add-booking-to-bookings-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql (scripts = "classpath:databases/bookings/delete-from-bookings.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateBookingById_validBookingRequestDto_success() throws Exception {
        BookingRequestDto requestDto = TestDataProvider.createValidUpdatedBookingRequestDto();
        BookingResponseDto expected = TestDataProvider.createUpdatedBookingResponseDto();

        mockMvc.perform(put("/bookings/{id}", 2L)
                .content(objectMapper.writeValueAsString(requestDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        MvcResult getResult = mockMvc.perform(get("/bookings/{id}", 2L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookingResponseDto actual = objectMapper.readValue(getResult.getResponse()
                .getContentAsString(), BookingResponseDto.class);
        assertThat(actual)
            .isNotNull()
            .usingRecursiveComparison()
            .comparingOnlyFields("checkInDate",
                "checkOutDate", "accommodationId")
                .isEqualTo(expected);

    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("delete booking by id")
    @Sql (scripts = "classpath:databases/bookings/add-booking-to-bookings-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql (scripts = "classpath:databases/bookings/delete-from-bookings.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void deleteById_WithValidBookId_Success() throws Exception {
        mockMvc.perform(delete("/bookings/{id}", TEST_BOOKING_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
