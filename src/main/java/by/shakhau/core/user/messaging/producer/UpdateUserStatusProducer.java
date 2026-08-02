package by.shakhau.core.user.messaging.producer;

import by.shakhau.core.user.messaging.event.UserStatusUpdatedEvent;
import by.shakhau.core.user.messaging.exception.KafkaConnectionException;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateUserStatusProducer {

    private static final String TOPIC = "user.status.updated";
    private KafkaTemplate<String, UserStatusUpdatedEvent> template;

    public void send(UserStatusUpdatedEvent event) {
        try {
            template.send(TOPIC, event.getUserId().toString(), event).get();
        } catch (Exception e) {
            throw new KafkaConnectionException(e.getMessage(), e);
        }
    }
}
