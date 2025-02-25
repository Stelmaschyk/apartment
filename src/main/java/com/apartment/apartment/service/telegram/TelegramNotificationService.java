package com.apartment.apartment.service.telegram;

import com.apartment.apartment.model.Booking;
import com.apartment.apartment.model.Payment;

public interface TelegramNotificationService {
    void notifyAdmins(String message);

    void notifyUser(String chatId, String message);

    void notifyAboutNewBookingToAdmins(Booking booking);

    void notifyAboutPaymentToAdmins(Payment payment);

    void notifyAboutExpiredBookings(Booking booking);
}
