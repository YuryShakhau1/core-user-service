package by.shakhau.core.user.messaging.producer;

import by.shakhau.core.user.messaging.event.DeactivateUserCredentialsEvent;
import by.shakhau.core.user.messaging.event.UserUpdatedEvent;
import by.shakhau.core.user.messaging.exception.KafkaConnectionException;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeactivateUserCredentialsProducer {

    private static final String TOPIC = "user.credentials.deactivate";
    private final KafkaTemplate<String, DeactivateUserCredentialsEvent> template;

    public void send(DeactivateUserCredentialsEvent event) {
        try {
            template.send(TOPIC, event.getUserId().toString(), event).get();
        } catch (Exception e) {
            throw new KafkaConnectionException(e.getMessage(), e);
        }
    }
}
