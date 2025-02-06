package conroller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.apartment.apartment.ApartmentApplication;
import com.apartment.apartment.dto.payment.CancelPaymentResponseDto;
import com.apartment.apartment.dto.payment.PaymentRequestDto;
import com.apartment.apartment.dto.payment.PaymentResponseDto;
import com.apartment.apartment.dto.payment.PaymentResponseDtoWithoutSession;
import com.apartment.apartment.exception.PaymentException;
import com.apartment.apartment.model.Booking;
import com.apartment.apartment.model.Payment;
import com.apartment.apartment.service.payment.PaymentService;
import com.apartment.apartment.service.stripe.StripeServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.model.checkout.Session;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import util.TestControllerDataProvider;

@SpringBootTest(classes = ApartmentApplication.class)
public class PaymentControllerTest {
    protected static MockMvc mockMvc;
    private static final String TEST_USER_EMAIL = "user@gmail.com";
    private static final Long TEST_USER_ID = 1L;
    private static final String TEST_SESSION_ID =
            "cs_test_a1ADMDWWeKAzWJ6RwzeyjmDSjdy0rHsvOJTmUZfZmISHIh6PsawrGLKe0i";
    private static final String TEST_URL =
            "https://checkout.stripe.com/c/pay/cs_test_a1QpOwJuVdsnxVDllwnHk82";
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StripeServiceImpl stripeService;

    @Autowired
    private PaymentService paymentService;

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
                    "databases/user/controller/add-user-to-tables-users.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                new ClassPathResource(
                    "databases/accommodations/controller/add-accommodations-to-accommodations"
                        + "-table.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                new ClassPathResource(
                    "databases/bookings/controller/add-booking-to-bookings-table.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                new ClassPathResource(
                    "databases/roles/controller/add-roles.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                new ClassPathResource(
                    "databases/user/controller/add-roles-to users-to-users-roles-table.sql")
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

    @Test
    @WithUserDetails(value = TEST_USER_EMAIL)
    @Sql(scripts = "classpath:databases/payments/controller/delete-from-payments.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createPayment_withValidRequest_returnsPaymentResponseDto() throws Exception {
        Session mockSession = new Session();
        mockSession.setId(TEST_SESSION_ID);
        mockSession.setUrl(TEST_URL);
        when(stripeService.createSession(any(Payment.class), any(Booking.class)))
                .thenReturn(mockSession);
        PaymentRequestDto requestDto = TestControllerDataProvider.createValidPaymentRequestDto();
        PaymentResponseDto expected = TestControllerDataProvider.createValidPaymentResponseDto();
        MvcResult result = mockMvc.perform(post("/payments")
                .content(objectMapper.writeValueAsString(requestDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        PaymentResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), PaymentResponseDto.class
        );
        assertThat(actual)
            .isNotNull()
            .usingRecursiveComparison()
            .ignoringFields("bookingId", "url")
            .withComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .isEqualTo(expected);

        assertThat(actual.url()).isNotNull();
    }

    @Test
    @WithMockUser(username = "user")
    @Sql(scripts = "classpath:databases/payments/controller/delete-from-payments.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createPayment_withInvalidBookingId_returnsBadRequest() throws Exception {
        PaymentRequestDto requestDto = new PaymentRequestDto(-1L);

        mockMvc.perform(post("/payments")
                .content(objectMapper.writeValueAsString(requestDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(scripts = "classpath:databases/payments/controller/add-payment-to-payments-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getAllByUserId_withValidUserId_returnsListOfPayments() throws Exception {
        MvcResult result = mockMvc.perform(get("/payments")
                .param("user_id", TEST_USER_ID.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<PaymentResponseDtoWithoutSession> actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
            new TypeReference<>() {
            }
        );

        AssertionsForInterfaceTypes.assertThat(actual)
                .hasSize(3); // Перевіряємо, що список містить один елемент

    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @WithUserDetails(value = TEST_USER_EMAIL)
    @Sql(scripts = "classpath:databases/payments/controller/add-payment-to-payments-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:databases/payments/controller/delete-from-payments.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getAllByUserId_withInvalidUserId_returnsEmptyList() throws Exception {
        MvcResult result = mockMvc.perform(get("/payments")
                .param("user_id", "999") // Невірний userId
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<PaymentResponseDtoWithoutSession> actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
            new TypeReference<>() {
            }
        );

        AssertionsForInterfaceTypes.assertThat(actual).isEmpty();
    }

    @WithUserDetails(value = TEST_USER_EMAIL)
    @Test
    @Sql(scripts = "classpath:databases/payments/controller/delete-from-payments.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:databases/payments/controller/add-payment-to-payments-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void confirmPayment_withUnpaidSession_throwsPaymentException() {

        when(stripeService.isSessionPaid(TEST_SESSION_ID)).thenReturn(false);

        assertThatThrownBy(() -> paymentService.confirmPayment(TEST_SESSION_ID))
            .isInstanceOf(PaymentException.class)
                .hasMessage("Unpaid session found");
    }

    @WithUserDetails(value = TEST_USER_EMAIL)
    @Test
    @Sql(scripts = "classpath:databases/payments/controller/add-payment-to-payments-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:databases/payments/controller/delete-from-payments.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void cancelPayment_withValidSessionId_returnsCancelPaymentResponseDto() throws Exception {
        CancelPaymentResponseDto expected =
                TestControllerDataProvider.createValidCancelPaymentResponseDto();

        MvcResult result = mockMvc.perform(get("/payments/cancel")
                .param("session_id", TEST_SESSION_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CancelPaymentResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CancelPaymentResponseDto.class
        );

        assertThat(actual.bookingId())
                .isNotNull()
                .isEqualTo(expected.bookingId());

        assertThat(actual)
            .usingRecursiveComparison()
            .comparingOnlyFields("status")
                .isEqualTo(expected);
    }
}
