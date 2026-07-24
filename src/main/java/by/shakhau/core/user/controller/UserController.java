package by.shakhau.core.user.controller;

import by.shakhau.core.user.controller.dto.response.GetUserResponse;
import by.shakhau.core.user.controller.dto.resuest.CreateUserRequest;
import by.shakhau.core.user.controller.dto.resuest.UpdateUserRequest;
import by.shakhau.core.user.controller.mapper.UserDtoMapper;
import by.shakhau.core.user.service.UserService;
import by.shakhau.core.user.service.model.User;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private UserDtoMapper mapper;
    private UserService service;

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<GetUserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        User user = service.create(mapper.toUser(request));
        return ResponseEntity.ok(mapper.toGetUserResponse(user));
    }

    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<GetUserResponse> findUser(@PathVariable UUID id) {
        User user = service.findById(id);
        return ResponseEntity.ok(mapper.toGetUserResponse(user));
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<GetUserResponse>> findUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String surname,
            Pageable pageable) {
        Page<User> userPage = service.findAll(name, surname, pageable);
        return ResponseEntity.ok(userPage.map(mapper::toGetUserResponse));
    }

    @PutMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<GetUserResponse> updateUser(
            @PathVariable UUID id,
            @Valid
            @RequestBody UpdateUserRequest request) {
        User user = service.update(mapper.toUser(id, request));
        return ResponseEntity.ok(mapper.toGetUserResponse(user));
    }

    @PatchMapping(value = "/{id}")
    public ResponseEntity<Void> updateUserStatus(@PathVariable UUID id, @RequestParam Boolean active) {
        service.updateActiveStatus(id, active);
        return ResponseEntity.noContent().build();
    }
}
