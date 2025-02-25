package com.apartment.apartment.dto.accommodation;

import com.apartment.apartment.model.Accommodation;
import com.apartment.apartment.model.Address;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class AccommodationRequestDto {
    @NotNull
    @Positive
    private BigDecimal dailyRate;
    @NotNull
    @Positive
    private Integer availability;
    @NotBlank
    private Accommodation.AccommodationType type;
    @NotBlank
    private String size;
    @NotNull
    private Address address;
    @NotBlank
    private List<String> amenities;
}
