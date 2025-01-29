package com.apartment.apartment.service.accommodation;

import com.apartment.apartment.dto.accommodation.AccommodationRequestDto;
import com.apartment.apartment.dto.accommodation.AccommodationResponseDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface AccommodationService {
    AccommodationResponseDto save(AccommodationRequestDto requestDto);

    AccommodationResponseDto findById(Long id);

    List<AccommodationResponseDto> findAllAccommodations(Pageable pageable);

    AccommodationResponseDto update(Long id, AccommodationRequestDto requestDto);

    void deleteById(Long id);
}
