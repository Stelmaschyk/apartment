package com.apartment.apartment.repository.accommodation;

import com.apartment.apartment.model.Accommodation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {
}
