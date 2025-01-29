package com.apartment.apartment.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Table(name = "Accommodations")
@Entity
@RequiredArgsConstructor
public class Accommodation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private BigDecimal dailyRate;
    @NotNull
    private Integer availability;
    @NotNull
    private String size;
    @Embedded
    private Address address;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "accommodation_amenities",
            joinColumns = @JoinColumn(name = "accommodation_id"))
    @Column(name = "amenity")
    @NotNull
    private List<String> amenities = new ArrayList<>();

    @Getter
    public enum AccommodationType {
        HOUSE,
        GUESTHOUSE,
        APARTMENT,
        GLAMPING,
    }

    @Transient
    private String accommodationTypeName;

    public Accommodation(String accommodationTypeName) {
        this.accommodationTypeName = accommodationTypeName;
    }
}
