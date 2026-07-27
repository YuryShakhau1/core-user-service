package by.shakhau.core.user.service.impl;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Random;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class CommonTestUtil {

    private static final Random RANDOM = new Random();

    protected static final UUID USER_ID = UUID.randomUUID();
    protected static final UUID CARD_ID = UUID.randomUUID();

    protected static final String USER_FIRST_NAME = "John";
    protected static final String USER_LAST_NAME = "Doe";

    protected static final String CARD_NUMBER = "1111222233334444";
}
