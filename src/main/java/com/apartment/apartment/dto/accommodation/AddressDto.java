package com.apartment.apartment.dto.accommodation;

import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class AddressDto {
    private String country;
    private String city;
    private String street;
    private Integer buildNumber;
}
