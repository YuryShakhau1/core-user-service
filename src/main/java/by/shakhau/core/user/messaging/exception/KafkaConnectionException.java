package by.shakhau.core.user.messaging.exception;

public class KafkaConnectionException extends RuntimeException {
    public KafkaConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
