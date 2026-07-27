package by.shakhau.core.user.controller.mapper;

import by.shakhau.core.user.controller.dto.response.PaymentCardResponse;
import by.shakhau.core.user.controller.dto.resuest.CreatePaymentCardRequest;
import by.shakhau.core.user.controller.dto.resuest.UpdatePaymentCardRequest;
import by.shakhau.core.user.service.model.PaymentCard;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentCardDtoMapper {

    PaymentCardResponse toPaymentCardResponse(PaymentCard paymentCard);
    PaymentCard toPaymentCard(CreatePaymentCardRequest request);
    PaymentCard toPaymentCard(UUID id, UpdatePaymentCardRequest request);
}
