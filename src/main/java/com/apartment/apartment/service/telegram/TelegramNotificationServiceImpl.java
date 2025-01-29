package com.apartment.apartment.service.telegram;

import com.apartment.apartment.component.Bot;
import com.apartment.apartment.model.Booking;
import com.apartment.apartment.model.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TelegramNotificationServiceImpl implements TelegramNotificationService {
    private final Bot bot;

    @Override
    public void notifyAdmins(String message) {
        bot.sendMessageToAdmins(message);
    }

    @Override
    public void notifyUser(String chatId, String message) {
        bot.sendMessage(chatId, message);
    }

    public void notifyAboutNewBookingToAdmins(Booking booking) {
        String message = """ 
            Accommodation was successfully booked.
            Check in date: %s
            Check out date: %s
            AmountToPay, USD: %f
            Booking id: %d""".formatted(
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getTotalAmount(),
                booking.getId());
        notifyAdmins(message);
    }

    public void notifyAboutPaymentToAdmins(Payment payment) {
        String message = """ 
            Booking was successfully paid. Details:
            AmountToPay, USD: %f
            Accommodation id: %s""".formatted(
                payment.getBooking().getTotalAmount(),
                payment.getBooking().getId());
        notifyAdmins(message);
    }

    public void notifyAboutExpiredBookings(Booking booking) {
        String message = """ 
            Booking id: %d
            Accommodation id: %s,
            Check in date: %s
            Check out date: %s
            User id: %d""".formatted(
                booking.getId(),
                booking.getAccommodation().getId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getUser().getId());
        notifyAdmins(message);
    }
}
