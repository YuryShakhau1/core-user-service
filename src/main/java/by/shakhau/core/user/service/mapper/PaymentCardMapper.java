package by.shakhau.core.user.service.mapper;

import by.shakhau.core.user.repository.entity.PaymentCardEntity;
import by.shakhau.core.user.service.model.PaymentCard;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PaymentCardMapper {

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    PaymentCardEntity toEntity(PaymentCard paymentCard);
    PaymentCard toDomain(PaymentCardEntity entity);
    void updateEntity(PaymentCard user, @MappingTarget PaymentCardEntity entity);
}
