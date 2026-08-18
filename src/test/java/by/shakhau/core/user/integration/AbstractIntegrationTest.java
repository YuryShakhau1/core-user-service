package by.shakhau.core.user.integration;

import by.shakhau.core.user.messaging.consumer.CreatedUserCredentialsConsumer;
import by.shakhau.core.user.messaging.producer.CreateUserProducer;
import by.shakhau.core.user.messaging.producer.DeactivateUserCredentialsProducer;
import by.shakhau.core.user.messaging.producer.UpdateUserProducer;
import by.shakhau.core.user.messaging.producer.UpdateUserStatusProducer;
import by.shakhau.core.user.repository.PaymentCardRepository;
import by.shakhau.core.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class AbstractIntegrationTest {

    public static final String USER_ID = "userId";

    private UUID userId;

    @MockitoBean
    private CreateUserProducer createUserProducer;

    @MockitoBean
    private UpdateUserProducer updateUserProducer;

    @MockitoBean
    private UpdateUserStatusProducer updateUserStatusProducer;

    @MockitoBean
    private DeactivateUserCredentialsProducer deactivateUserCredentialsProducer;

    @MockitoBean
    private CreatedUserCredentialsConsumer createdUserCredentialsConsumer;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentCardRepository paymentCardRepository;

    @Autowired
    protected CacheManager cacheManager;

    @Container
    static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:18-alpine")
                    .withDatabaseName("test-db")
                    .withUsername("test-user")
                    .withPassword("test-password");

    @Container
    static final GenericContainer<?> redis =
            new GenericContainer<>("redis:8.8-alpine")
                    .withExposedPorts(6379);

    static {
        postgres.start();
        redis.start();
    }

    public UUID getUserId() {
        return userId;
    }

    @BeforeEach
    public void setUp() throws Exception {
        paymentCardRepository.deleteAll();
        userRepository.deleteAll();
        cacheManager.getCacheNames().forEach(cacheName -> cacheManager.getCache(cacheName).clear());
        userId = createUser();
    }

    @DynamicPropertySource
    static void configureDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    protected UUID createUser() throws Exception {
        String request = """
                {
                    "firstName": "John",
                    "lastName": "Doe",
                    "birthDate": "1995-05-10",
                    "email": "john@mail.com",
                    "active": true
                }
                """;

        String response =
                mockMvc.perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("role", "ROLE_ADMIN")
                                .content(request))
                        .andExpect(status().isCreated())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        return UUID.fromString(objectMapper.readTree(response).get("id").asText());
    }
}
