package service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.apartment.apartment.dto.accommodation.AccommodationRequestDto;
import com.apartment.apartment.dto.accommodation.AccommodationResponseDto;
import com.apartment.apartment.dto.accommodation.AddressDto;
import com.apartment.apartment.exception.EntityNotFoundException;
import com.apartment.apartment.mapper.AccommodationMapper;
import com.apartment.apartment.model.Accommodation;
import com.apartment.apartment.model.Address;
import com.apartment.apartment.repository.accommodation.AccommodationRepository;
import com.apartment.apartment.service.accommodation.AccommodationService;
import com.apartment.apartment.service.accommodation.AccommodationServiceImpl;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class AccommodationServiceTest {
    private static final Long TEST_ACCOMMODATION_ID = 1L;
    private static Accommodation accommodation;
    private static Accommodation updatedAccommodation;
    private static AccommodationResponseDto responseDto;
    private static AccommodationResponseDto updatedResponseDto;
    private static AccommodationRequestDto requestDto;
    private static AccommodationRequestDto updatedRequestDto;

    @Mock
    private AccommodationRepository accommodationRepository;
    @Mock
    private AccommodationMapper accommodationMapper;
    @InjectMocks
    private AccommodationServiceImpl accommodationService;

    @BeforeAll
    static void setup() {
        Address address = new Address()
            .setCountry("Germany")
            .setCity("Berlin")
            .setStreet("Main Street")
            .setBuildNumber(10);

        Address updatedAddress = new Address()
            .setCountry("Germany")
            .setCity("Hamburg")
            .setStreet("Updated Street")
            .setBuildNumber(20);

        List<String> amenities = List.of("WiFi", "TV", "Parking");
        List<String> updatedAmenities = List.of("WiFi", "TV", "Parking", "Pool");

        accommodation = new Accommodation()
            .setId(1L)
            .setDailyRate(BigDecimal.valueOf(100.00))
            .setAvailability(5)
            .setAccommodationType(Accommodation.AccommodationType.APARTMENT)
            .setSize("50m²")
            .setAddress(address)
            .setAmenities(amenities);

        updatedAccommodation = new Accommodation()
            .setId(1L)
            .setDailyRate(BigDecimal.valueOf(150.00))
            .setAvailability(3)
            .setAccommodationType(Accommodation.AccommodationType.HOUSE)
            .setSize("100m²")
            .setAddress(updatedAddress)
            .setAmenities(updatedAmenities);

        responseDto = new AccommodationResponseDto()
            .setId(accommodation.getId())
            .setDailyRate(accommodation.getDailyRate())
            .setAvailability(accommodation.getAvailability())
            .setType(accommodation.getAccommodationType())
            .setSize(accommodation.getSize())
            .setAddress(accommodation.getAddress())
            .setAmenities(accommodation.getAmenities());

        updatedResponseDto = new AccommodationResponseDto()
            .setId(updatedAccommodation.getId())
            .setDailyRate(updatedAccommodation.getDailyRate())
            .setAvailability(updatedAccommodation.getAvailability())
            .setType(updatedAccommodation.getAccommodationType())
            .setSize(updatedAccommodation.getSize())
            .setAddress(updatedAccommodation.getAddress())
            .setAmenities(updatedAccommodation.getAmenities());

        requestDto = new AccommodationRequestDto()
            .setDailyRate(BigDecimal.valueOf(100.00))
            .setAvailability(5)
            .setType(Accommodation.AccommodationType.APARTMENT)
            .setSize("50m²")
            .setAddress(address)
            .setAmenities(amenities);

        updatedRequestDto = new AccommodationRequestDto()
            .setDailyRate(BigDecimal.valueOf(150.00))
            .setAvailability(3)
            .setType(Accommodation.AccommodationType.HOUSE)
            .setSize("100m²")
            .setAddress(updatedAddress)
            .setAmenities(updatedAmenities);
    }

    @Test
    void saveAccommodation_withValidAccommodationRequestDto_shouldReturnResponseDto() {
        when(accommodationMapper.toModel(requestDto)).thenReturn(accommodation);
        when(accommodationRepository.save(accommodation)).thenReturn(accommodation);
        when(accommodationMapper.toDto(accommodation)).thenReturn(responseDto);

        AccommodationResponseDto result = accommodationService.save(requestDto);

        assertThat(result)
            .isEqualTo(responseDto);

        verify(accommodationRepository).save(accommodation);
        verify(accommodationMapper).toModel(requestDto);
        verify(accommodationMapper).toDto(accommodation);
    }

    @Test
    void findById_shouldReturnAccommodationResponseDto() {
        when(accommodationRepository.findById(TEST_ACCOMMODATION_ID))
            .thenReturn(Optional.of(accommodation));
        when(accommodationMapper.toDto(accommodation)).thenReturn(responseDto);

        AccommodationResponseDto result = accommodationService.findById(TEST_ACCOMMODATION_ID);

        assertThat(result).isEqualTo(responseDto);
        verify(accommodationRepository).findById(TEST_ACCOMMODATION_ID);
        verify(accommodationMapper).toDto(accommodation);
    }

    @Test
    void findById_shouldThrowExceptionIfNotFound() {
        when(accommodationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
            () -> accommodationService.findById(TEST_ACCOMMODATION_ID));

        verify(accommodationRepository).findById(1L);
    }

    @Test
    void findAllAccommodations_shouldReturnListOfDtos() {
        List<Accommodation> accommodations = List.of(accommodation);
        List<AccommodationResponseDto> responseDtos = List.of(responseDto);
        Pageable pageable = PageRequest.of(0, 10);

        when(accommodationRepository.findAll(pageable)).thenReturn(new PageImpl<>(accommodations));
        when(accommodationMapper.toListDto(accommodations)).thenReturn(responseDtos);

        List<AccommodationResponseDto> result =
            accommodationService.findAllAccommodations(pageable);

        assertThat(result).isEqualTo(responseDtos);
        verify(accommodationRepository).findAll(pageable);
        verify(accommodationMapper).toListDto(accommodations);
    }

    @Test
    void update_shouldReturnUpdatedAccommodationResponseDto() {
        when(accommodationRepository.findById(TEST_ACCOMMODATION_ID))
            .thenReturn(Optional.of(accommodation));
        doNothing().when(accommodationMapper)
            .updateAccommodationFromDto(updatedRequestDto, accommodation);
        when(accommodationMapper.toAddressDto(updatedRequestDto.getAddress()))
            .thenReturn(new AddressDto());
        when(accommodationRepository.save(accommodation)).thenReturn(updatedAccommodation);
        when(accommodationMapper.toDto(updatedAccommodation)).thenReturn(updatedResponseDto);

        AccommodationResponseDto result =
            accommodationService.update(TEST_ACCOMMODATION_ID, updatedRequestDto);

        assertThat(result).isEqualTo(updatedResponseDto);
        verify(accommodationRepository).findById(TEST_ACCOMMODATION_ID);
        verify(accommodationMapper).updateAccommodationFromDto(updatedRequestDto, accommodation);
        verify(accommodationRepository).save(accommodation);
        verify(accommodationMapper).toDto(updatedAccommodation);
    }

    @Test
    void update_shouldThrowExceptionIfNotFound() {
        when(accommodationRepository.findById(TEST_ACCOMMODATION_ID))
            .thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
            () -> accommodationService.update(TEST_ACCOMMODATION_ID, updatedRequestDto));

        verify(accommodationRepository).findById(TEST_ACCOMMODATION_ID);
    }

    @Test
    void deleteById_shouldCallRepositoryDeleteMethod() {
        doNothing().when(accommodationRepository).deleteById(TEST_ACCOMMODATION_ID);

        accommodationService.deleteById(TEST_ACCOMMODATION_ID);

        verify(accommodationRepository).deleteById(TEST_ACCOMMODATION_ID);
    }
}
