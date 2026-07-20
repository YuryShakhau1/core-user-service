package by.shakhau.core.user.controller.mapper;

import by.shakhau.core.user.controller.dto.response.GetUserResponse;
import by.shakhau.core.user.controller.dto.resuest.CreateUserRequest;
import by.shakhau.core.user.controller.dto.resuest.UpdateUserRequest;
import by.shakhau.core.user.service.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserDtoMapper {

    GetUserResponse toGetUserResponse(User user);
    User toUser(CreateUserRequest request);
    User toUser(UpdateUserRequest request);
}
