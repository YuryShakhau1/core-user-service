package by.shakhau.core.user.messaging.consumer;

import by.shakhau.core.user.messaging.event.UserRegisteredEvent;
import by.shakhau.core.user.messaging.mapper.UserEventMapper;
import by.shakhau.core.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRegistrationConsumer {

    private static final String TOPIC = "user.registered";

    private final UserEventMapper userEventMapper;
    private final UserService userService;

    @KafkaListener(topics = TOPIC, groupId = "user-service")
    public void consume(UserRegisteredEvent event, Acknowledgment ack) {
        userService.create(userEventMapper.toUser(event));
        ack.acknowledge();
    }
}
