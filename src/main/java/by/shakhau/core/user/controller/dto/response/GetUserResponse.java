package by.shakhau.core.user.controller.dto.response;

import java.time.LocalDate;
import java.util.UUID;

public record GetUserResponse(
        UUID id,
        String name,
        String surname,
        LocalDate birthDate,
        String email,
        Boolean active) {
}
