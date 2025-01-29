package com.apartment.apartment.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Entity
@Accessors(chain = true)
@Table(name = "payments")
@RequiredArgsConstructor
public class Payment {
    @Id
    private Long id;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;
    @Column(nullable = false)
    private String url;
    @Column(nullable = false)
    private String sessionId;
    @Column(nullable = false)
    private BigDecimal amount;

    public enum PaymentStatus {
        PENDING,
        PAID,
        CANCELED
    }
}
