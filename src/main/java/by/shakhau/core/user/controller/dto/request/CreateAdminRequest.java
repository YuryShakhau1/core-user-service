package by.shakhau.core.user.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAdminRequest extends CreateUserRequest {

    @NotBlank(message = "Admin init secret is required")
    private String adminInitSecret;
}
