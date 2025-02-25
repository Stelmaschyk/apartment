package com.apartment.apartment.mapper;

import com.apartment.apartment.config.MapperConfig;
import com.apartment.apartment.dto.payment.CancelPaymentResponseDto;
import com.apartment.apartment.dto.payment.PaymentResponseDto;
import com.apartment.apartment.dto.payment.PaymentResponseDtoWithoutSession;
import com.apartment.apartment.model.Payment;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {
    @Mapping(source = "payment.id", target = "booking_id")
    PaymentResponseDto toDto(Payment payment);

    @Mapping(source = "payment.id", target = "bookingId")
    PaymentResponseDtoWithoutSession toPaymentInfoDto(Payment payment);

    @Mapping(source = "payment.id", target = "bookingId")
    CancelPaymentResponseDto toCancelDto(Payment payment);

    List<PaymentResponseDtoWithoutSession> toPaymentInfoListDto(List<Payment> payments);
}
