package com.apartment.apartment.dto.booking;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class BookingRequestDto {
    @NotBlank
    private LocalDate checkInDate;
    @NotBlank
    private LocalDate checkOutDate;
    @NotBlank
    private Long accommodationId;
}
