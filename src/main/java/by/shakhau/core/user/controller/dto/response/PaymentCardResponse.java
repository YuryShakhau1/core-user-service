package by.shakhau.core.user.controller.dto.response;

import java.time.LocalDate;
import java.util.UUID;

public record PaymentCardResponse(
        UUID id,
        String number,
        String holder,
        LocalDate expirationDate,
        Boolean active) {
}
