package com.apartment.apartment.dto.payment;

import java.math.BigDecimal;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public record PaymentResponseDtoWithoutSession(Long bookingId,
                                               String status,
                                               BigDecimal amount) {
}
