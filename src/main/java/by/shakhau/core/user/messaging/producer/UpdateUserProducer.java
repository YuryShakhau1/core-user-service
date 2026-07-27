package by.shakhau.core.user.messaging.producer;

import by.shakhau.core.user.messaging.event.UserCreatedEvent;
import by.shakhau.core.user.messaging.event.UserUpdatedEvent;
import by.shakhau.core.user.messaging.exception.KafkaConnectionException;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UpdateUserProducer {

    private static final String TOPIC = "user.updated";
    private KafkaTemplate<String, UserUpdatedEvent> template;

    public void send(UserUpdatedEvent event) {
        try {
            template.send(TOPIC, event.getUserId().toString(), event).get();
        } catch (Exception e) {
            throw new KafkaConnectionException(e.getMessage(), e);
        }
    }
}
