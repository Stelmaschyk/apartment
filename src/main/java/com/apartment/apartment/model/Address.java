package com.apartment.apartment.model;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Embeddable
@Accessors(chain = true)
public class Address {
    private String country;
    private String city;
    private String street;
    private Integer buildNumber;
}
