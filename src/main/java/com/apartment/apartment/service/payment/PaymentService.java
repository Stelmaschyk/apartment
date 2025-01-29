package com.apartment.apartment.service.payment;

import com.apartment.apartment.dto.payment.CancelPaymentResponseDto;
import com.apartment.apartment.dto.payment.PaymentResponseDto;
import com.apartment.apartment.dto.payment.PaymentResponseDtoWithoutSession;
import java.util.List;

public interface PaymentService {
    PaymentResponseDto createPayment(Long bookingId, Long userId);

    List<PaymentResponseDtoWithoutSession> findAllByUserId(Long userId);

    PaymentResponseDtoWithoutSession confirmPayment(String sessionId);

    CancelPaymentResponseDto cancelPayment(String sessionId);
}
