package by.shakhau.core.user.controller.mapper;

import by.shakhau.core.user.controller.dto.response.GetUserResponse;
import by.shakhau.core.user.controller.dto.resuest.CreateUserRequest;
import by.shakhau.core.user.controller.dto.resuest.UpdateUserRequest;
import by.shakhau.core.user.service.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserDtoMapper {

    GetUserResponse toGetUserResponse(User user);
    User toUser(CreateUserRequest request);
    User toUser(Long id, UpdateUserRequest request);
}
