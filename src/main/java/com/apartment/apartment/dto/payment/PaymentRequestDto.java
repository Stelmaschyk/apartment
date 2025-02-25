package com.apartment.apartment.dto.payment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public record PaymentRequestDto(@NotNull @Positive Long bookingId) {
}
