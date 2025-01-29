package com.apartment.apartment.repository.booking.spec;

import com.apartment.apartment.model.Booking;
import com.apartment.apartment.repository.SpecificationProvider;
import java.util.Arrays;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class UserIdSpecificationProvider implements SpecificationProvider<Booking> {
    @Override
    public String getKey() {
        return "userId";
    }

    public Specification<Booking> getSpecification(String[] params) {
        return (root, query, criteriaBuilder)
                -> root.get("userId").in(Arrays.stream(params).toArray());
    }
}
