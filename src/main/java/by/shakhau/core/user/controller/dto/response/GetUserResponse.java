package by.shakhau.core.user.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
public class GetUserResponse {

    private final Long id;
    private final String name;
    private final String surname;
    private final LocalDate birthDate;
    private final String email;
    private final Boolean active;
}
