package by.shakhau.core.user.controller.mapper;

import by.shakhau.core.user.controller.dto.response.UserResponse;
import by.shakhau.core.user.controller.dto.request.CreateUserRequest;
import by.shakhau.core.user.controller.dto.request.UpdateUserRequest;
import by.shakhau.core.user.service.model.CreatedUser;
import by.shakhau.core.user.service.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserDtoMapper {

    UserResponse toUserResponse(CreatedUser user);
    UserResponse toUserResponse(User user);
    User toUser(CreateUserRequest request);
    User toUser(UUID id, UpdateUserRequest request);
}
