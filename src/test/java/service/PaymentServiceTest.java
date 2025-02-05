package service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.apartment.apartment.dto.payment.*;
import com.apartment.apartment.mapper.PaymentMapper;
import com.apartment.apartment.model.Accommodation;
import com.apartment.apartment.model.Booking;
import com.apartment.apartment.model.Payment;
import com.apartment.apartment.repository.booking.BookingRepository;
import com.apartment.apartment.repository.payment.PaymentRepository;
import com.apartment.apartment.service.payment.PaymentServiceImpl;
import com.apartment.apartment.service.stripe.StripeServiceImpl;
import com.stripe.model.checkout.Session;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
    private static final Long TEST_BOOKING_ID = 1L;
    private static final Long TEST_USER_ID = 2L;
    private static final String TEST_SESSION_ID = "session_123";

    private static Booking booking;
    private static Payment payment;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private StripeServiceImpl stripeService;

    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @BeforeEach
    void setup() {
        Accommodation accommodation = new Accommodation()
            .setId(1L)
            .setDailyRate(BigDecimal.valueOf(100));

        booking = new Booking()
            .setId(TEST_BOOKING_ID)
            .setId(TEST_USER_ID)
            .setStatus(Booking.BookingStatus.PENDING)
            .setCheckInDate(LocalDate.now())
            .setCheckOutDate(LocalDate.now().plusDays(3))
            .setAccommodation(accommodation);

        payment = new Payment()
            .setAmount(booking.getTotalAmount())
            .setBooking(booking)
            .setStatus(Payment.PaymentStatus.PENDING)
            .setSessionId(TEST_SESSION_ID)
            .setUrl("https://payment.url");
    }

    @Test
    void createPayment_shouldReturnPaymentResponseDto() {
        Session session = mock(Session.class);
        when(session.getId()).thenReturn(TEST_SESSION_ID);
        when(session.getUrl()).thenReturn("https://payment.url");

        when(bookingRepository.findByIdAndUserIdAndStatus(TEST_BOOKING_ID, TEST_USER_ID,
            Booking.BookingStatus.PENDING))
            .thenReturn(Optional.of(booking));
        when(stripeService.createSession(any(Payment.class), eq(booking))).thenReturn(session);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(paymentMapper.toDto(payment)).thenReturn(new PaymentResponseDto(booking.getId(),
            "PENDING", booking.getTotalAmount(), "https://payment.url"));

        PaymentResponseDto result = paymentService.createPayment(TEST_BOOKING_ID, TEST_USER_ID);

        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo("PENDING");
        verify(bookingRepository).findByIdAndUserIdAndStatus(TEST_BOOKING_ID, TEST_USER_ID,
            Booking.BookingStatus.PENDING);
        verify(stripeService).createSession(any(Payment.class), eq(booking));
        verify(paymentRepository).save(any(Payment.class));
        verify(paymentMapper).toDto(payment);
    }

    @Test
    void confirmPayment_shouldUpdateStatusAndReturnPaymentResponseDto() {
        when(stripeService.isSessionPaid(TEST_SESSION_ID)).thenReturn(true);
        when(paymentRepository.findBySessionIdAndStatusIs(TEST_SESSION_ID,
            Payment.PaymentStatus.PENDING))
            .thenReturn(Optional.of(payment));
        when(paymentMapper.toPaymentInfoDto(payment))
            .thenReturn(new PaymentResponseDtoWithoutSession(booking.getId(), "PAID",
                booking.getTotalAmount()));

        PaymentResponseDtoWithoutSession result = paymentService.confirmPayment(TEST_SESSION_ID);

        assertThat(result).isNotNull();
        assertThat(payment.getStatus()).isEqualTo(Payment.PaymentStatus.PAID);
        assertThat(payment.getBooking().getStatus()).isEqualTo(Booking.BookingStatus.CONFIRMED);
        verify(stripeService).isSessionPaid(TEST_SESSION_ID);
        verify(paymentRepository).findBySessionIdAndStatusIs(TEST_SESSION_ID,
            Payment.PaymentStatus.PENDING);
        verify(paymentMapper).toPaymentInfoDto(payment);
    }

    @Test
    void cancelPayment_shouldUpdateStatusAndReturnCancelPaymentResponseDto() {
        when(paymentRepository.findBySessionIdAndStatusIs(TEST_SESSION_ID,
            Payment.PaymentStatus.PENDING))
            .thenReturn(Optional.of(payment));
        when(paymentRepository.save(payment)).thenReturn(payment);
        when(paymentMapper.toCancelDto(payment))
            .thenReturn(new CancelPaymentResponseDto(booking.getId(), "CANCELED",
                booking.getTotalAmount()));

        CancelPaymentResponseDto result = paymentService.cancelPayment(TEST_SESSION_ID);

        assertThat(result).isNotNull();
        assertThat(payment.getStatus()).isEqualTo(Payment.PaymentStatus.CANCELED);
        verify(paymentRepository).findBySessionIdAndStatusIs(TEST_SESSION_ID,
            Payment.PaymentStatus.PENDING);
        verify(paymentRepository).save(payment);
        verify(paymentMapper).toCancelDto(payment);
    }
}
