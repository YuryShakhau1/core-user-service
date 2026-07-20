package by.shakhau.core.user.service.mapper;

import by.shakhau.core.user.repository.entity.PaymentCardEntity;
import by.shakhau.core.user.repository.entity.UserEntity;
import by.shakhau.core.user.service.model.User;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    UserEntity toEntity(User user);
    User toDomain(UserEntity entity);

    @AfterMapping
    default void linkCardBackReference(@MappingTarget UserEntity user) {
        for (PaymentCardEntity paymentCard : user.getPaymentCards()) {
            paymentCard.setUser(user);
        }
    }
}
