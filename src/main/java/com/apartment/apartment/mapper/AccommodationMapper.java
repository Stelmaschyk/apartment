package com.apartment.apartment.mapper;

import com.apartment.apartment.config.MapperConfig;
import com.apartment.apartment.dto.accommodation.AccommodationRequestDto;
import com.apartment.apartment.dto.accommodation.AccommodationResponseDto;
import com.apartment.apartment.dto.accommodation.AddressDto;
import com.apartment.apartment.model.Accommodation;
import com.apartment.apartment.model.Address;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(config = MapperConfig.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccommodationMapper {
    @Mapping(source = "amenities", target = "amenities")
    @Mapping(source = "accommodationType", target = "type")
    AccommodationResponseDto toDto(Accommodation accommodation);

    List<AccommodationResponseDto> toListDto(List<Accommodation> accommodations);

    @Mapping(source = "amenities", target = "amenities")
    @Mapping(source = "type", target = "accommodationType")
    Accommodation toModel(AccommodationRequestDto requestDto);

    @Mapping(source = "amenities", target = "amenities")
    void updateAccommodationFromDto(AccommodationRequestDto requestDto,
                           @MappingTarget Accommodation accommodation);

    Address toAddress(Address addressDto);

    AddressDto toAddressDto(Address address);
}
