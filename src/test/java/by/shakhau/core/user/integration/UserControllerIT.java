package by.shakhau.core.user.integration;

import by.shakhau.core.user.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
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
                    "name": "John",
                    "surname": "Doe",
                    "birthDate": "1995-05-10",
                    "email": "john@mail.com",
                    "active": true
                }
                """;

        String response =
                mockMvc.perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id").exists())
                        .andExpect(jsonPath("$.name")
                                .value("John"))
                        .andExpect(jsonPath("$.surname")
                                .value("Doe"))
                        .andExpect(jsonPath("$.email")
                                .value("john@mail.com"))
                        .andExpect(jsonPath("$.active")
                                .value(true))
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        JsonNode json = objectMapper.readTree(response);
        Long id = json.get("id").asLong();

        assertThat(userRepository.findById(id)).isPresent();
    }

    @Test
    void shouldFindUserByIdWhenUserExists() throws Exception {
        Long id = createUser();

        mockMvc.perform(get("/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(id))
                .andExpect(jsonPath("$.name")
                        .value("John"))
                .andExpect(jsonPath("$.email")
                        .value("john@mail.com"));
    }

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        mockMvc.perform(get("/users/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldFindUsersWhenUsersExist() throws Exception {
        createUser();

        mockMvc.perform(get("/users")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content")
                        .isArray())
                .andExpect(jsonPath("$.content[0].name")
                        .value("John"));
    }

    @Test
    void shouldUpdateUserWhenRequestIsValid() throws Exception {
        Long id = createUser();

        String request = """
                {
                    "id": %d,
                    "name": "Petr",
                    "surname": "Petrov",
                    "birthDate": "1990-01-01",
                    "email": "petr@mail.com"
                }
                """.formatted(id);

        mockMvc.perform(put("/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(id))
                .andExpect(jsonPath("$.name")
                        .value("Petr"))
                .andExpect(jsonPath("$.surname")
                        .value("Petrov"))
                .andExpect(jsonPath("$.active")
                        .value(true));

        var user = userRepository.findById(id).orElseThrow();

        assertThat(user.getName()).isEqualTo("Petr");
        assertThat(user.getActive()).isTrue();
    }

    @Test
    void shouldUpdateUserStatusWhenActiveIsFalse() throws Exception {
        Long id = createUser();

        mockMvc.perform(patch("/users/{id}", id)
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
                    "name": "",
                    "surname": "",
                    "birthDate": null,
                    "email": "wrong-email",
                    "active": null
                }
                """;

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
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
                """;

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
