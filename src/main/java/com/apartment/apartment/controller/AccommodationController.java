package com.apartment.apartment.controller;

import com.apartment.apartment.dto.accommodation.AccommodationRequestDto;
import com.apartment.apartment.dto.accommodation.AccommodationResponseDto;
import com.apartment.apartment.model.Accommodation;
import com.apartment.apartment.service.accommodation.AccommodationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Accommodation management",
        description = "Endpoints for managing accommodations.")
@RequiredArgsConstructor
@RestController
@RequestMapping("/accommodations")
public class AccommodationController {
    private final AccommodationService accommodationService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create and save new accommodation")
    public AccommodationResponseDto save(@RequestBody AccommodationRequestDto requestDto) {
        return accommodationService.save(requestDto);
    }

    @GetMapping
    @Operation(summary = "Get all accommodation with pagination")
    @ResponseStatus(HttpStatus.OK)
    public List<AccommodationResponseDto> getAllAccommodations(Pageable pageable) {
        return accommodationService.findAllAccommodations(pageable);
    }

    @GetMapping
    @RequestMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get accommodation by id")
    public AccommodationResponseDto getAccommodationById(@PathVariable Long id) {
        return accommodationService.findById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Update exist accommodation in DB by id")
    public AccommodationResponseDto updateAccommodation(
            @PathVariable Long id, @RequestBody AccommodationRequestDto updateDto) {
        return accommodationService.update(id, updateDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Delete accommodation by id")
    public void deleteAccommodationById(@PathVariable Long id) {
        accommodationService.deleteById(id);
    }

    @GetMapping("/types")
    @Operation(summary = "Get AccomodationType list")
    public ResponseEntity<List<String>> getAccommodationTypes() {
        List<String> types = Arrays.stream(Accommodation.AccommodationType.values())
                .map(Enum::name)
                .toList();
        return ResponseEntity.ok(types);
    }
}
