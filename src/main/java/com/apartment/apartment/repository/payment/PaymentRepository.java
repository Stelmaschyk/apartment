package com.apartment.apartment.repository.payment;

import com.apartment.apartment.model.Payment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findAllByBookingUserId(Long userId);

    @EntityGraph(attributePaths = "booking")
    Optional<Payment> findBySessionIdAndStatusIs(String sessionId,
                                                 Payment.PaymentStatus status);
}
