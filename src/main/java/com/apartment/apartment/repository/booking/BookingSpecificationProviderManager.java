package com.apartment.apartment.repository.booking;

import com.apartment.apartment.model.Booking;
import com.apartment.apartment.repository.SpecificationProvider;
import com.apartment.apartment.repository.SpecificationProviderManager;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookingSpecificationProviderManager implements SpecificationProviderManager<Booking> {
    private final List<SpecificationProvider<Booking>> specificationProviders;

    @Override
    public SpecificationProvider<Booking> getSpecificationProvider(String key) {
        return specificationProviders.stream()
            .filter(provider -> provider.getKey().equals(key)).findFirst()
            .orElseThrow(()
                    -> new NoSuchElementException("No specification provider found for key: "
                + key));
    }
}
