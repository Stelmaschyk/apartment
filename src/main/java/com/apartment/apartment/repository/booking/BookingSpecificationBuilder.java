package com.apartment.apartment.repository.booking;

import com.apartment.apartment.dto.booking.BookingSearchParametersDto;
import com.apartment.apartment.model.Booking;
import com.apartment.apartment.repository.SpecificationBuilder;
import com.apartment.apartment.repository.SpecificationProviderManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookingSpecificationBuilder implements SpecificationBuilder<Booking> {
    public static final String USER_ID = "userId";
    public static final String STATUS = "status";
    private final SpecificationProviderManager<Booking> bookingSpecificationProviderManager;

    @Override
    public Specification<Booking> build(BookingSearchParametersDto searchParameters) {
        Specification<Booking> spec = Specification.where(null);
        if (searchParameters.userId() != null && searchParameters.userId().length > 0) {
            spec =
                spec.and(bookingSpecificationProviderManager.getSpecificationProvider(USER_ID)
                    .getSpecification(searchParameters.userId()));
        }
        if (searchParameters.status() != null && searchParameters.status().length > 0) {
            spec =
                spec.and(bookingSpecificationProviderManager.getSpecificationProvider(STATUS)
                    .getSpecification(searchParameters.status()));
        }
        return spec;
    }
}
