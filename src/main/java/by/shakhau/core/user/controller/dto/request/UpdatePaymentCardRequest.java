package by.shakhau.core.user.controller.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class UpdatePaymentCardRequest {

    @NotBlank(message = "Card number is required")
    @Pattern(regexp = "^\\d{16}$", message = "Card number must contain exactly 16 digits")
    private String number;

    @NotBlank(message = "Holder is required")
    @Size(min = 1, max = 100, message = "Card holder must be between 1 and 50 characters")
    private String holder;

    @NotNull(message = "Card expiration date is required")
    @FutureOrPresent(message = "The card expiration date must be in the future")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expirationDate;

    @NotNull(message = "Card active is required")
    private Boolean active;
}
