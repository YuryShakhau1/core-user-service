package by.shakhau.core.user.service.mapper;

import by.shakhau.core.user.repository.entity.UserEntity;
import by.shakhau.core.user.service.model.CreatedUser;
import by.shakhau.core.user.service.model.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(source = "firstName", target = "name")
    @Mapping(source = "lastName", target = "surname")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    UserEntity toEntity(User user);

    @Mapping(source = "name", target = "firstName")
    @Mapping(source = "surname", target = "lastName")
    User toDomain(UserEntity entity);

    CreatedUser toCreatedUser(User user, StringBuilder tempPassword);

    @Mapping(source = "firstName", target = "name")
    @Mapping(source = "lastName", target = "surname")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(User user, @MappingTarget UserEntity entity);
}
