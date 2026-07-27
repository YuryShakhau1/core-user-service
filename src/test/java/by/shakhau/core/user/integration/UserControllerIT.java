package by.shakhau.core.user.integration;

import by.shakhau.core.user.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldCreateUserWhenRequestIsValid() throws Exception {
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
                                .header(AUTHORIZATION, AUTHORIZATION_HEADER)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("role", "ROLE_ADMIN")
                                .content(request))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.id").exists())
                        .andExpect(jsonPath("$.firstName").value("John"))
                        .andExpect(jsonPath("$.lastName").value("Doe"))
                        .andExpect(jsonPath("$.birthDate").value("1995-05-10"))
                        .andExpect(jsonPath("$.email").value("john@mail.com"))
                        .andExpect(jsonPath("$.active").value(true))
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        JsonNode json = objectMapper.readTree(response);
        UUID id = UUID.fromString(json.get("id").asText());

        assertThat(userRepository.findById(id)).isPresent();
    }

    @Test
    void shouldFindUserByIdWhenUserExists() throws Exception {
        UUID id = createUser();

        mockMvc.perform(get("/users/{id}", id)
                        .header(AUTHORIZATION, AUTHORIZATION_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john@mail.com"));
    }

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        mockMvc.perform(get("/users/{id}", UUID.randomUUID())
                        .header(AUTHORIZATION, AUTHORIZATION_HEADER))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldFindUsersWhenUsersExist() throws Exception {
        createUser();

        mockMvc.perform(get("/users")
                        .header(AUTHORIZATION, AUTHORIZATION_HEADER)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].firstName").value("John"));
    }

    @Test
    void shouldUpdateUserWhenRequestIsValid() throws Exception {
        UUID id = createUser();

        String request = """
                {
                    "id": "%s",
                    "firstName": "Petr",
                    "lastName": "Petrov",
                    "birthDate": "1990-01-01",
                    "email": "petr@mail.com"
                }
                """.formatted(id);

        mockMvc.perform(put("/users/{id}", id)
                        .header(AUTHORIZATION, AUTHORIZATION_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.firstName").value("Petr"))
                .andExpect(jsonPath("$.lastName").value("Petrov"))
                .andExpect(jsonPath("$.active").value(true));

        var user = userRepository.findById(id).orElseThrow();

        assertThat(user.getName()).isEqualTo("Petr");
        assertThat(user.getActive()).isTrue();
    }

    @Test
    void shouldUpdateUserStatusWhenActiveIsFalse() throws Exception {
        UUID id = createUser();

        mockMvc.perform(patch("/users/{id}", id)
                        .header(AUTHORIZATION, AUTHORIZATION_HEADER)
                        .param("active", "false"))
                .andExpect(status().isNoContent());

        assertThat(userRepository.findById(id)
                .orElseThrow()
                .getActive())
                .isFalse();
    }


    @Test
    void shouldReturnBadRequestWhenCreateRequestIsInvalid() throws Exception {
        String request = """
                {
                    "firstName": "",
                    "lastName": "",
                    "birthDate": null,
                    "email": "wrong-email",
                    "active": null
                }
                """;

        mockMvc.perform(post("/users")
                        .header(AUTHORIZATION, AUTHORIZATION_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }
}
