package by.shakhau.core.user.messaging.consumer;

import by.shakhau.core.user.messaging.event.DeactivateUserCredentialsEvent;
import by.shakhau.core.user.messaging.event.UserCredentialsCreatedEvent;
import by.shakhau.core.user.messaging.producer.DeactivateUserCredentialsProducer;
import by.shakhau.core.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CreatedUserCredentialsConsumer {

    private static final String TOPIC = "user.credentials.created";

    private final UserService userService;
    private final DeactivateUserCredentialsProducer deactivateUserCredentialsProducer;

    @KafkaListener(topics = TOPIC, groupId = "auth-service")
    public void consume(UserCredentialsCreatedEvent event, Acknowledgment ack) {
        if (!userService.existsById(event.getUserId())) {
            if (event.isCreated()) {
                deactivateUserCredentialsProducer.send(new DeactivateUserCredentialsEvent(event.getUserId()));
            }

            ack.acknowledge();
            return;
        }

        userService.updateActiveStatus(event.getUserId(), event.isCreated());

        ack.acknowledge();
    }
}
