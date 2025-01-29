package com.apartment.apartment.dto.accommodation;

import com.apartment.apartment.model.Address;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class AccommodationRequestDto {
    private BigDecimal dailyRate;
    private Integer availability;
    private String size;
    private Address address;
    private List<String> amenities;
}
