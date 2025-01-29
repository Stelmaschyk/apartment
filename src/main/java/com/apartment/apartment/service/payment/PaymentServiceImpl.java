package com.apartment.apartment.service.payment;

import static com.apartment.apartment.model.Payment.PaymentStatus.CANCELED;
import static com.apartment.apartment.model.Payment.PaymentStatus.PAID;
import static com.apartment.apartment.model.Payment.PaymentStatus.PENDING;

import com.apartment.apartment.dto.payment.CancelPaymentResponseDto;
import com.apartment.apartment.dto.payment.PaymentResponseDto;
import com.apartment.apartment.dto.payment.PaymentResponseDtoWithoutSession;
import com.apartment.apartment.exception.EntityNotFoundException;
import com.apartment.apartment.exception.PaymentException;
import com.apartment.apartment.mapper.PaymentMapper;
import com.apartment.apartment.model.Booking;
import com.apartment.apartment.model.Payment;
import com.apartment.apartment.repository.booking.BookingRepository;
import com.apartment.apartment.repository.payment.PaymentRepository;
import com.apartment.apartment.service.stripe.StripeServiceImpl;
import com.apartment.apartment.service.telegram.TelegramNotificationService;
import com.stripe.model.checkout.Session;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final BookingRepository bookingRepository;
    private final StripeServiceImpl stripeService;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final TelegramNotificationService telegramNotificationService;

    @Override
    @Transactional
    public PaymentResponseDto createPayment(Long bookingId, Long userId) {
        Booking booking = bookingRepository
                .findByIdAndUserIdAndStatus(bookingId, userId,
                    Booking.BookingStatus.PENDING)
                .orElseThrow(() -> new EntityNotFoundException("""
                            Booking with id '%d" for user with id wasn't '%d' found"""
                .formatted(bookingId, userId)));
        Payment payment = new Payment();
        payment.setAmount(booking.getTotalAmount());
        payment.setBooking(booking);
        payment.setStatus(PENDING);
        Session session = stripeService.createSession(payment, booking);
        payment.setSessionId(session.getId());
        payment.setUrl(session.getUrl());
        return paymentMapper.toDto(paymentRepository.save(payment));
    }

    @Override
    public List<PaymentResponseDtoWithoutSession> findAllByUserId(Long userId) {
        return paymentMapper
            .toPaymentInfoListDto(paymentRepository.findAllByBookingUserId(userId));
    }

    @Override
    @Transactional
    public PaymentResponseDtoWithoutSession confirmPayment(String sessionId) {
        if (!stripeService.isSessionPaid(sessionId)) {
            throw new PaymentException("Unpaid session found");
        }
        Payment payment = paymentRepository
                .findBySessionIdAndStatusIs(sessionId, PENDING)
                .orElseThrow(() -> new EntityNotFoundException(
                    "Wasn't found unpaid payment session id '%s'".formatted(sessionId)));
        payment.setStatus(PAID);
        payment.getBooking()
                .setStatus(Booking.BookingStatus.CONFIRMED);
        telegramNotificationService.notifyAboutPaymentToAdmins(payment);
        return paymentMapper.toPaymentInfoDto(payment);
    }

    @Override
    public CancelPaymentResponseDto cancelPayment(String sessionId) {
        Payment payment = paymentRepository.findBySessionIdAndStatusIs(sessionId, PENDING)
                .orElseThrow(() -> new EntityNotFoundException(
                      "Wasn't found unpaid payment session id '%s'".formatted(sessionId)));
        payment.setStatus(CANCELED);
        return paymentMapper.toCancelDto(paymentRepository.save(payment));
    }
}
