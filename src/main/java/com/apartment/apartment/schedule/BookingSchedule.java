package com.apartment.apartment.schedule;

import com.apartment.apartment.model.Booking;
import com.apartment.apartment.service.booking.BookingService;
import com.apartment.apartment.service.telegram.TelegramNotificationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingSchedule {
    private final BookingService bookingService;
    private final TelegramNotificationService telegramNotificationService;

    @Scheduled(cron = "0 5 0 * * *", zone = "Europe/Kiev")
    public void sendBookingExpiredNotification() {
        List<Booking> expiredBookings = bookingService.expiredBookings();

        if (expiredBookings.isEmpty()) {
            telegramNotificationService.notifyAdmins("No expired bookings today!");
        } else {
            for (Booking booking : expiredBookings) {
                telegramNotificationService.notifyAboutExpiredBookings(booking);
            }
        }
    }
}
