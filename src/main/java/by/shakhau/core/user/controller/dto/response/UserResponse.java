package by.shakhau.core.user.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserResponse(
        UUID id,
        String firstName,
        String lastName,
        LocalDate birthDate,
        String email,
        Boolean active,
        String tempPassword) {
}
