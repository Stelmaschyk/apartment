package com.apartment.apartment.service.stripe;

import static com.stripe.param.checkout.SessionCreateParams.LineItem;
import static com.stripe.param.checkout.SessionCreateParams.Mode;
import static com.stripe.param.checkout.SessionCreateParams.builder;

import com.apartment.apartment.exception.PaymentException;
import com.apartment.apartment.model.Booking;
import com.apartment.apartment.model.Payment;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class StripeServiceImpl {
    private static final String PAYMENTS_PATH = "/payments/";
    private static final String SUCCESS_PATH = "success";
    private static final String CANCEL_PATH = "cancel";
    private static final String QUERY_PARAM_NAME = "sessionId";
    private static final String QUERY_PARAM_VALUE = "{CHECKOUT_SESSION_ID}";
    private static final String SESSION_STATUS_PAID = "paid";
    private static final Long DEFAULT_QUANTITY = 1L;
    private static final String CURRENCY = "USD";
    private final String httpUrl;

    public StripeServiceImpl(@Value("${stripe.secretKey}") String stripeSecret,
                             @Value("${app.url}") String appUrl) {
        Stripe.apiKey = stripeSecret;
        httpUrl = appUrl;
    }

    public Session createSession(Payment payment, Booking booking) {
        var sessionParams = builder()
                .setMode(Mode.PAYMENT)
                .setSuccessUrl(constructSessionUri(SUCCESS_PATH))
                .setCancelUrl(constructSessionUri(CANCEL_PATH))
                .addLineItem(LineItem.builder()
                .setQuantity(DEFAULT_QUANTITY)
                .setPriceData(LineItem.PriceData.builder()
                    .setCurrency(CURRENCY)
                    .setUnitAmountDecimal(StripeUtil.unitAmountInCentToDollars(payment))
                    .setProductData(LineItem.PriceData.ProductData.builder()
                        .setName(StripeUtil.getAccommodationName(booking))
                        .setDescription(StripeUtil.getBookingDescription(booking))
                        .build())
                    .build())
                .build())
                .build();
        try {
            return Session.create(sessionParams);
        } catch (StripeException e) {
            throw new PaymentException("Failed to create Stripe session");
        }
    }

    private String constructSessionUri(final String segmentPath) {
        return UriComponentsBuilder
            .fromHttpUrl(httpUrl)
            .path(PAYMENTS_PATH)
            .pathSegment(segmentPath)
            .queryParam(QUERY_PARAM_NAME, QUERY_PARAM_VALUE)
            .build()
            .toUriString();
    }

    private Session retrieveSession(String sessionId) {
        try {
            return Session.retrieve(sessionId);
        } catch (StripeException e) {
            throw new PaymentException("Failed to find Stripe session");
        }
    }

    public boolean isSessionPaid(String sessionId) {
        return retrieveSession(sessionId)
            .getPaymentStatus()
            .equalsIgnoreCase(SESSION_STATUS_PAID);
    }

    private static class StripeUtil {
        private static String getBookingDescription(Booking booking) {
            return """
                Check in date: %s%n
                Check out date: %s%n
                Accommodation include the following amenities: %s"""
                .formatted(booking.getCheckInDate(),
                    booking.getCheckOutDate(),
                    String.join(", ", booking
                        .getAccommodation().getAmenities()));
        }

        private static String getAccommodationName(Booking booking) {
            return "Booking %s"
                .formatted(booking.getAccommodation().getAccommodationTypeName());

        }

        private static BigDecimal unitAmountInCentToDollars(Payment payment) {
            return payment.getAmount()
                .multiply(BigDecimal.valueOf(100L));
        }
    }
}
