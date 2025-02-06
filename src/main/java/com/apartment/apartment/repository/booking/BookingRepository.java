package com.apartment.apartment.repository.booking;

import com.apartment.apartment.model.Booking;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>,
        JpaSpecificationExecutor<Booking> {

    Optional<Booking> findByIdAndUserIdAndStatus(Long id, Long userId,
                                                   Booking.BookingStatus status);

    List<Booking> findAllByStatusNotInAndCheckOutDate(Collection<Booking.BookingStatus> statuses,
                                                      @NotNull LocalDate checkOutDate);
}
