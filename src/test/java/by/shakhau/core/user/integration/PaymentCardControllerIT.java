package by.shakhau.core.user.integration;

import by.shakhau.core.user.repository.PaymentCardRepository;
import by.shakhau.core.user.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PaymentCardControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentCardRepository paymentCardRepository;

    private record TestPaymentCard(UUID userId, UUID cardId) {
    }

    @Test
    void shouldCreatePaymentCardWhenRequestIsValid() throws Exception {
        UUID userId = createUser();

        String request = """
                {
                    "number": "1234567890123456",
                    "holder": "JOHN DOE",
                    "expirationDate": "2230-12-31",
                    "active": true
                }
                """;

        String response =
                mockMvc.perform(post("/payment-cards/users/{userId}", userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(AUTHORIZATION, AUTHORIZATION_HEADER)
                                .content(request))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.id").exists())
                        .andExpect(jsonPath("$.number").value("1234567890123456"))
                        .andExpect(jsonPath("$.holder").value("JOHN DOE"))
                        .andExpect(jsonPath("$.active").value(true))
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        JsonNode json = objectMapper.readTree(response);

        UUID cardId = UUID.fromString(json.get("id").asText());

        assertThat(paymentCardRepository.findById(cardId)).isPresent();
    }

    @Test
    void shouldFindPaymentCardByIdWhenCardExists() throws Exception {
        TestPaymentCard card = createPaymentCard();
        UUID cardId = card.cardId();

        mockMvc.perform(get("/payment-cards/{id}", cardId)
                        .header(AUTHORIZATION, AUTHORIZATION_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cardId.toString()))
                .andExpect(jsonPath("$.number").value("1234567890123456"))
                .andExpect(jsonPath("$.holder").value("JOHN DOE"));
    }

    @Test
    void shouldReturnNotFoundWhenPaymentCardDoesNotExist() throws Exception {
        mockMvc.perform(get("/payment-cards/{id}", UUID.randomUUID())
                        .header(AUTHORIZATION, AUTHORIZATION_HEADER))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldFindPaymentCardsByUserIdWhenCardsExist() throws Exception {
        TestPaymentCard card = createPaymentCard();
        UUID userId = card.userId();

        mockMvc.perform(get("/payment-cards/users/{userId}", userId)
                        .header(AUTHORIZATION, AUTHORIZATION_HEADER)
                        .param("active", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].number").value("1234567890123456"));
    }

    @Test
    void shouldFindAllPaymentCardsWhenCardsExist() throws Exception {
        createPaymentCard();

        mockMvc.perform(get("/payment-cards")
                        .header(AUTHORIZATION, AUTHORIZATION_HEADER)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].holder").value("JOHN DOE"));
    }

    @Test
    void shouldUpdatePaymentCardWhenRequestIsValid() throws Exception {
        TestPaymentCard card = createPaymentCard();
        UUID cardId = card.cardId();
        UUID userId = card.userId();

        String request = """
                {
                    "number": "9999888877776666",
                    "holder": "PETR PETROV",
                    "expirationDate": "2035-01-01"
                }
                """;

        mockMvc.perform(put("/payment-cards/{id}/users/{userId}", cardId, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, AUTHORIZATION_HEADER)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cardId.toString()))
                .andExpect(jsonPath("$.number").value("9999888877776666"))
                .andExpect(jsonPath("$.holder").value("PETR PETROV"))
                .andExpect(jsonPath("$.active").value(true));

        var updatedCard = paymentCardRepository.findById(cardId).orElseThrow();

        assertThat(updatedCard.getNumber()).isEqualTo("9999888877776666");
        assertThat(updatedCard.getActive()).isTrue();
    }


    @Test
    void shouldUpdatePaymentCardStatusWhenRequestIsValid() throws Exception {
        TestPaymentCard card = createPaymentCard();
        UUID cardId = card.cardId();

        mockMvc.perform(patch("/payment-cards/{id}/status", cardId)
                        .header(AUTHORIZATION, AUTHORIZATION_HEADER)
                        .param("active", "false"))
                .andExpect(status().isNoContent());

        assertThat(paymentCardRepository.findById(cardId)
                .orElseThrow()
                .getActive())
                .isFalse();
    }

    @Test
    void shouldReturnBadRequestWhenCreatePaymentCardRequestIsInvalid() throws Exception {
        UUID userId = createUser();

        String request = """
                {
                    "number": "",
                    "holder": "",
                    "expirationDate": null,
                    "active": null
                }
                """;

        mockMvc.perform(post("/payment-cards/users/{userId}", userId)
                        .header(AUTHORIZATION, AUTHORIZATION_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    private TestPaymentCard createPaymentCard() throws Exception {
        UUID userId = createUser();

        String request = """
                {
                    "number": "1234567890123456",
                    "holder": "JOHN DOE",
                    "expirationDate": "2230-12-31",
                    "active": true
                }
                """;

        String response = mockMvc.perform(post("/payment-cards/users/{userId}", userId)
                        .header(AUTHORIZATION, AUTHORIZATION_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UUID cardId = UUID.fromString(objectMapper.readTree(response).get("id").asText());

        return new TestPaymentCard(userId, cardId);
    }

    @Test
    void shouldFindPaymentCardsWhenBothFirstAndLastNameFiltersAreApplied() throws Exception {
        UUID userJohnDoeId = getUserId();
        String cardJohnDoe = """
            {
                "number": "1111222233334444",
                "holder": "JOHN DOE",
                "expirationDate": "2230-12-31",
                "active": true
            }
            """;
        mockMvc.perform(post("/payment-cards/users/{userId}", userJohnDoeId)
                        .header(AUTHORIZATION, AUTHORIZATION_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cardJohnDoe))
                .andExpect(status().isCreated());

        String userJohnSmithRequest = """
            {
                "firstName": "John",
                "lastName": "Smith",
                "birthDate": "1990-01-01",
                "email": "smith_card@mail.com",
                "active": true
            }
            """;
        String smithResponse = mockMvc.perform(post("/users")
                        .header(AUTHORIZATION, AUTHORIZATION_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("role", "ROLE_USER")
                        .content(userJohnSmithRequest))
                .andReturn().getResponse().getContentAsString();
        UUID userJohnSmithId = UUID.fromString(objectMapper.readTree(smithResponse).get("id").asText());

        String cardJohnSmith = """
            {
                "number": "5555666677778888",
                "holder": "JOHN SMITH",
                "expirationDate": "2230-12-31",
                "active": true
            }
            """;
        mockMvc.perform(post("/payment-cards/users/{userId}", userJohnSmithId)
                .header(AUTHORIZATION, AUTHORIZATION_HEADER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(cardJohnSmith));

        mockMvc.perform(get("/payment-cards")
                        .header(AUTHORIZATION, AUTHORIZATION_HEADER)
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.numberOfElements").value(1))
                .andExpect(jsonPath("$.content[0].holder").value("JOHN DOE"));
    }

    @Test
    void shouldReturnForbiddenWhenUserTriesToUpdateStatusOfSomeoneElsesCard() throws Exception {
        TestPaymentCard strangerCard = createPaymentCard();
        UUID strangerCardId = strangerCard.cardId();

        Claims claims = mock(Claims.class);
        when(claims.getExpiration()).thenReturn(new Date(System.currentTimeMillis() + 1000000));
        when((List<String>) claims.get("roles")).thenReturn(Collections.singletonList("ROLE_USER"));
        when(claims.getSubject()).thenReturn(getUserId().toString()); // Токен принадлежит "нам"
        when(jwtService.getClaims(any())).thenReturn(claims);

        mockMvc.perform(patch("/payment-cards/{id}/status", strangerCardId)
                        .header(AUTHORIZATION, AUTHORIZATION_HEADER)
                        .param("active", "false"))
                .andExpect(status().isForbidden());

        assertThat(paymentCardRepository.findById(strangerCardId).orElseThrow().getActive())
                .isTrue();
    }
}
