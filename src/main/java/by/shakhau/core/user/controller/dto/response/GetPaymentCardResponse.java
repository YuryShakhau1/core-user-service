package by.shakhau.core.user.controller.dto.response;

import java.time.LocalDate;
import java.util.UUID;

public record GetPaymentCardResponse(
        UUID id,
        String number,
        LocalDate expirationDate,
        Boolean active) {
};
