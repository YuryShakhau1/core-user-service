package by.shakhau.core.user.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
public class GetPaymentCardResponse {

    private Long id;
    private String number;
    private String holder;
    private LocalDate expirationDate;
    private Boolean active;
}
