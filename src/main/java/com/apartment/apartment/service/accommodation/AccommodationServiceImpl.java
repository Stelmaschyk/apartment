package com.apartment.apartment.service.accommodation;

import com.apartment.apartment.dto.accommodation.AccommodationRequestDto;
import com.apartment.apartment.dto.accommodation.AccommodationResponseDto;
import com.apartment.apartment.exception.EntityNotFoundException;
import com.apartment.apartment.mapper.AccommodationMapper;
import com.apartment.apartment.model.Accommodation;
import com.apartment.apartment.repository.accommodation.AccommodationRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccommodationServiceImpl implements AccommodationService {
    private final AccommodationRepository accommodationRepository;
    private final AccommodationMapper accommodationMapper;

    @Transactional
    @Override
    public AccommodationResponseDto save(AccommodationRequestDto requestDto) {
        Accommodation accommodation = accommodationMapper.toModel(requestDto);
        return accommodationMapper.toDto(accommodationRepository.save(accommodation));
    }

    @Override
    public AccommodationResponseDto findById(Long id) {
        return accommodationMapper.toDto(findAccommodationById(id));
    }

    @Override
    public List<AccommodationResponseDto> findAllAccommodations(Pageable pageable) {
        List<Accommodation> accommodationDtos =
                accommodationRepository.findAll(pageable).getContent();
        return accommodationMapper.toListDto(accommodationDtos);
    }

    @Transactional
    @Override
    public AccommodationResponseDto update(Long id, AccommodationRequestDto updateDto) {
        Accommodation accommodation = findAccommodationById(id);
        accommodationMapper.updateAccommodationFromDto(updateDto, accommodation);
        accommodationMapper.toAddressDto(updateDto.getAddress());
        return accommodationMapper.toDto(accommodationRepository.save(accommodation));
    }

    @Override
    public void deleteById(Long id) {
        accommodationRepository.deleteById(id);
    }

    private Accommodation findAccommodationById(Long id) {
        return accommodationRepository.findById(id).orElseThrow(
            () -> new EntityNotFoundException("Can`t find accommodation with id: %d"
                                                                                .formatted(id)));
    }
}
