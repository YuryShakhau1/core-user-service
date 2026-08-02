package by.shakhau.core.user.controller;

import by.shakhau.core.user.controller.dto.request.CreateUserRequest;
import by.shakhau.core.user.controller.dto.request.UpdateUserRequest;
import by.shakhau.core.user.controller.dto.response.UserResponse;
import by.shakhau.core.user.controller.filter.JwtAuthenticationFilter.UserPrincipal;
import by.shakhau.core.user.controller.mapper.UserDtoMapper;
import by.shakhau.core.user.service.UserService;
import by.shakhau.core.user.service.model.CreatedUser;
import by.shakhau.core.user.service.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
@RequiredArgsConstructor
public class UserController {

    private final UserDtoMapper mapper;
    private final UserService service;

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody
            CreateUserRequest request,
            @RequestParam
            String role) {
        CreatedUser user = service.createAndRegister(mapper.toUser(request), role);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toUserResponse(user));
    }

    @GetMapping(value = "/me", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> findCurrentUser(@AuthenticationPrincipal UserPrincipal principal) {
        UUID userId = principal.getId();
        return ResponseEntity.ok(mapper.toUserResponse(service.findById(userId)));
    }

    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> findUser(@PathVariable UUID id) {
        return ResponseEntity.ok(mapper.toUserResponse(service.findById(id)));
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<UserResponse>> findUsers(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            Pageable pageable) {
        Page<User> userPage = service.findAll(firstName, lastName, pageable);
        return ResponseEntity.ok(userPage.map(mapper::toUserResponse));
    }

    @PutMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable UUID id,
            @Valid
            @RequestBody UpdateUserRequest request) {
        User user = service.update(mapper.toUser(id, request));
        return ResponseEntity.ok(mapper.toUserResponse(user));
    }

    @PatchMapping(value = "/{id}")
    public ResponseEntity<Void> updateUserStatus(@PathVariable UUID id, @RequestParam Boolean active) {
        service.updateActiveStatus(id, active);
        return ResponseEntity.noContent().build();
    }
}
