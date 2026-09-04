package by.shakhau.core.user.messaging.mapper;

import by.shakhau.core.user.messaging.event.UserCreatedEvent;
import by.shakhau.core.user.messaging.event.UserUpdatedEvent;
import by.shakhau.core.user.repository.entity.UserEntity;
import by.shakhau.core.user.service.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserEventMapper {

    @Mapping(source = "id", target = "userId")
    UserCreatedEvent toUserCreatedEvent(User user);

    @Mapping(source = "name", target = "firstName")
    @Mapping(source = "surname", target = "lastName")
    @Mapping(source = "id", target = "userId")
    UserUpdatedEvent toUserUpdatedEvent(UserEntity user);
}
