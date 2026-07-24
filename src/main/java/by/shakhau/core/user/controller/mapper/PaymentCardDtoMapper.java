package by.shakhau.core.user.controller.mapper;

import by.shakhau.core.user.controller.dto.response.GetPaymentCardResponse;
import by.shakhau.core.user.controller.dto.resuest.CreatePaymentCardRequest;
import by.shakhau.core.user.controller.dto.resuest.UpdatePaymentCardRequest;
import by.shakhau.core.user.service.model.PaymentCard;
import org.mapstruct.Mapper;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface PaymentCardDtoMapper {

    GetPaymentCardResponse toGetPaymentCardResponse(PaymentCard paymentCard);
    PaymentCard toPaymentCard(CreatePaymentCardRequest request);
    PaymentCard toPaymentCard(UUID id, UpdatePaymentCardRequest request);
}
