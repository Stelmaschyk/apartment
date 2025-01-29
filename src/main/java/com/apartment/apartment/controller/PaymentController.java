package com.apartment.apartment.controller;

import com.apartment.apartment.dto.payment.CancelPaymentResponseDto;
import com.apartment.apartment.dto.payment.PaymentRequestDto;
import com.apartment.apartment.dto.payment.PaymentResponseDto;
import com.apartment.apartment.dto.payment.PaymentResponseDtoWithoutSession;
import com.apartment.apartment.model.User;
import com.apartment.apartment.service.payment.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Payments management",
        description = "Endpoints for managing payments.")
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Initialize payment",
            description = "Starts the payment process"
                + " for a specific booking only for register users")
    public PaymentResponseDto createPayment(@RequestBody @Valid PaymentRequestDto requestDto,
                                            @AuthenticationPrincipal User user) {
        return paymentService
            .createPayment(requestDto.bookingId(), user.getId());
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Getting user's payment information by user_id",
            description = "Getting information about user's payments by user_id. Only for admins.")
    @ResponseStatus(HttpStatus.OK)
    public List<PaymentResponseDtoWithoutSession> getAllByUserId(
            @RequestParam(name = "user_id") Long userId) {
        return paymentService.findAllByUserId(userId);
    }

    @GetMapping("/success")
    @ResponseStatus(HttpStatus.OK)
    public PaymentResponseDtoWithoutSession completePayment(
            @RequestParam(name = "session_id") String sessionId) {
        return paymentService.confirmPayment(sessionId);
    }

    @GetMapping("/cancel")
    @ResponseStatus(HttpStatus.OK)
    public CancelPaymentResponseDto cancelPayment(
            @RequestParam(name = "session_id") String sessionId) {
        return paymentService.cancelPayment(sessionId);
    }
}
