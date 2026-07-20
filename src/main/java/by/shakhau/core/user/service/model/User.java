package by.shakhau.core.user.service.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class User {

    private Long id;
    private String name;
    private String surname;
    private LocalDate birthDate;
    private String email;
    private Boolean active;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
