package by.shakhau.core.user.service.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class PaymentCard {

    private UUID id;
    private String number;
    private String holder;
    private LocalDate expirationDate;
    private Boolean active;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
