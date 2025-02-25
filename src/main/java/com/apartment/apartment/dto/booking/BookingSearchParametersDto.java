package com.apartment.apartment.dto.booking;

import lombok.experimental.Accessors;

@Accessors(chain = true)
public record BookingSearchParametersDto(String[] userId, String[] status) {
}
