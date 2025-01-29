package com.apartment.apartment.mapper;

import com.apartment.apartment.config.MapperConfig;
import com.apartment.apartment.dto.booking.BookingRequestDto;
import com.apartment.apartment.dto.booking.BookingResponseDto;
import com.apartment.apartment.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(config = MapperConfig.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookingMapper {

    @Mapping(source = "accommodationId", target = "accommodation.id")
    Booking toModel(BookingRequestDto requestDto);

    @Mapping(source = "booking.id", target = "bookingId")
    @Mapping(source = "accommodation.id", target = "accommodationId")
    @Mapping(source = "user.id", target = "userId")
    BookingResponseDto toDto(Booking booking);

    void updateDto(@MappingTarget Booking booking, BookingRequestDto requestDto);
}
