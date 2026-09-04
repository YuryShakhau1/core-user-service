package by.shakhau.core.user.messaging.event;

import by.shakhau.core.user.service.model.serialization.SafePasswordDeserializer;
import by.shakhau.core.user.service.model.serialization.SafePasswordSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UserCreatedEvent {

    private UUID userId;
    private String firstName;
    private String lastName;
    private String email;

    @JsonDeserialize(using = SafePasswordDeserializer.class)
    @JsonSerialize(using = SafePasswordSerializer.class)
    private StringBuilder tempPassword;
    private String role;
}
