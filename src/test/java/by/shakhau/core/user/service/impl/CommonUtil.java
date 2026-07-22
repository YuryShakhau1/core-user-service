package by.shakhau.core.user.service.impl;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Random;

@ExtendWith(MockitoExtension.class)
public class CommonUtil {

    private static final Random RANDOM = new Random();

    // Just for view convenience
    private static final Long ID_RANDOM_LIMIT = 100L;

    protected static final Long USER_ID = RANDOM.nextLong(ID_RANDOM_LIMIT);
    protected static final Long CARD_ID = RANDOM.nextLong(ID_RANDOM_LIMIT) + 1L;

    protected static final String USER_NAME = "John";
    protected static final String USER_SURNAME = "Doe";

    protected static final String CARD_NUMBER = "1111222233334444";
}
