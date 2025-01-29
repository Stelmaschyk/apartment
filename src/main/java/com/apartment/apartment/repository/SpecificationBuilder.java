package com.apartment.apartment.repository;

import com.apartment.apartment.dto.booking.BookingSearchParametersDto;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationBuilder<T> {
    Specification<T> build(BookingSearchParametersDto searchParameters);
}
