package com.apartment.apartment.dto.payment;

import java.math.BigDecimal;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public record PaymentResponseDto(Long booking_id,
                                 String status,
                                 BigDecimal amount,
                                 String url) {
}
