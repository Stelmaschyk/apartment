package com.apartment.apartment.dto.accommodation;

import com.apartment.apartment.model.Accommodation;
import com.apartment.apartment.model.Address;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class AccommodationResponseDto {
    private Long id;
    private BigDecimal dailyRate;
    private Integer availability;
    private Accommodation.AccommodationType type;
    private String size;
    private Address address;
    private List<String> amenities;
}
