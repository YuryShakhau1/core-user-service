package by.shakhau.core.user.controller.mapper;

import by.shakhau.core.user.controller.dto.response.GetPaymentCardResponse;
import by.shakhau.core.user.controller.dto.resuest.CreatePaymentCardRequest;
import by.shakhau.core.user.controller.dto.resuest.UpdatePaymentCardRequest;
import by.shakhau.core.user.service.model.PaymentCard;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentCardDtoMapper {

    GetPaymentCardResponse toGetPaymentCardResponse(PaymentCard paymentCard);
    PaymentCard toPaymentCard(CreatePaymentCardRequest request);
    PaymentCard toPaymentCard(Long id, UpdatePaymentCardRequest request);
}
