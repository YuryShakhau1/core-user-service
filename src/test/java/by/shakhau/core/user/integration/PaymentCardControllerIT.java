package by.shakhau.core.user.integration;

import by.shakhau.core.user.repository.PaymentCardRepository;
import by.shakhau.core.user.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PaymentCardControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentCardRepository paymentCardRepository;

    private record TestPaymentCard(Long userId, Long cardId) {}

    @Test
    void shouldCreatePaymentCardWhenRequestIsValid() throws Exception {
        Long userId = createUser();

        String request = """
                {
                    "number": "1234567890123456",
                    "holder": "JOHN DOE",
                    "expirationDate": "2230-12-31",
                    "active": true
                }
                """;

        String response =
                mockMvc.perform(post("/payment-card/users/{userId}", userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id").exists())
                        .andExpect(jsonPath("$.number")
                                .value("1234567890123456"))
                        .andExpect(jsonPath("$.holder")
                                .value("JOHN DOE"))
                        .andExpect(jsonPath("$.active")
                                .value(true))
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        JsonNode json = objectMapper.readTree(response);

        Long cardId = json.get("id").asLong();

        assertThat(paymentCardRepository.findById(cardId)).isPresent();
    }

    @Test
    void shouldFindPaymentCardByIdWhenCardExists() throws Exception {
        TestPaymentCard card = createPaymentCard();
        Long cardId = card.cardId();

        mockMvc.perform(get("/payment-card/{id}", cardId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(cardId))
                .andExpect(jsonPath("$.number")
                        .value("1234567890123456"))
                .andExpect(jsonPath("$.holder")
                        .value("JOHN DOE"));
    }

    @Test
    void shouldReturnNotFoundWhenPaymentCardDoesNotExist() throws Exception {
        mockMvc.perform(get("/payment-card/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldFindPaymentCardsByUserIdWhenCardsExist() throws Exception {
        TestPaymentCard card = createPaymentCard();
        Long userId = card.userId();

        mockMvc.perform(get("/payment-card/users/{userId}", userId)
                        .param("active", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].number").value("1234567890123456"));
    }

    @Test
    void shouldFindAllPaymentCardsWhenCardsExist() throws Exception {
        createPaymentCard();

        mockMvc.perform(get("/payment-card")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content")
                        .isArray())
                .andExpect(jsonPath("$.content[0].holder")
                        .value("JOHN DOE"));
    }

    @Test
    void shouldUpdatePaymentCardWhenRequestIsValid() throws Exception {
        TestPaymentCard card = createPaymentCard();
        Long cardId = card.cardId();
        Long userId = card.userId();

        String request = """
                {
                    "number": "9999888877776666",
                    "holder": "PETR PETROV",
                    "expirationDate": "2035-01-01"
                }
                """;

        mockMvc.perform(put("/payment-card/{id}/users/{userId}", cardId, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(cardId))
                .andExpect(jsonPath("$.number")
                        .value("9999888877776666"))
                .andExpect(jsonPath("$.holder")
                        .value("PETR PETROV"))
                .andExpect(jsonPath("$.active")
                        .value(true));


        var updatedCard = paymentCardRepository.findById(cardId).orElseThrow();

        assertThat(updatedCard.getNumber()).isEqualTo("9999888877776666");
        assertThat(updatedCard.getActive()).isTrue();
    }


    @Test
    void shouldUpdatePaymentCardStatusWhenRequestIsValid() throws Exception {
        TestPaymentCard card = createPaymentCard();
        Long cardId = card.cardId();

        mockMvc.perform(patch("/payment-card/{id}/status", cardId)
                        .param("active", "false"))
                .andExpect(status().isNoContent());

        assertThat(paymentCardRepository.findById(cardId)
                .orElseThrow()
                .getActive())
                .isFalse();
    }

    @Test
    void shouldReturnBadRequestWhenCreatePaymentCardRequestIsInvalid() throws Exception {
        Long userId = createUser();

        String request = """
                {
                    "number": "",
                    "holder": "",
                    "expirationDate": null,
                    "active": null
                }
                """;

        mockMvc.perform(post("/payment-card/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    private TestPaymentCard createPaymentCard() throws Exception {
        Long userId = createUser();

        String request = """
            {
                "number": "1234567890123456",
                "holder": "JOHN DOE",
                "expirationDate": "2230-12-31",
                "active": true
            }
            """;

        String response = mockMvc.perform(post("/payment-card/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long cardId = objectMapper.readTree(response).get("id").asLong();

        return new TestPaymentCard(userId, cardId);
    }

    private Long createUser() throws Exception {
        String request = """
                {
                    "name": "John",
                    "surname": "Doe",
                    "birthDate": "1995-05-10",
                    "email": "john@mail.com",
                    "active": true
                }
                """.formatted(System.nanoTime());

        String response =
                mockMvc.perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        return objectMapper.readTree(response).get("id").asLong();
    }
}
