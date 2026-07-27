package by.shakhau.core.user.controller;

import by.shakhau.core.user.controller.dto.response.PaymentCardResponse;
import by.shakhau.core.user.controller.dto.resuest.CreatePaymentCardRequest;
import by.shakhau.core.user.controller.dto.resuest.UpdatePaymentCardRequest;
import by.shakhau.core.user.controller.mapper.PaymentCardDtoMapper;
import by.shakhau.core.user.service.PaymentCardService;
import by.shakhau.core.user.service.UserService;
import by.shakhau.core.user.service.impl.JwtService;
import by.shakhau.core.user.service.model.PaymentCard;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/payment-cards")
public class PaymentCardController extends AbstractSecurityController {

    private final PaymentCardDtoMapper mapper;
    private final UserService userService;
    private final PaymentCardService service;

    public PaymentCardController(
            JwtService jwtService,
            PaymentCardDtoMapper mapper,
            UserService userService,
            PaymentCardService service) {
        super(jwtService);
        this.mapper = mapper;
        this.userService = userService;
        this.service = service;
    }

    @PostMapping(value = "/users/{userId}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<PaymentCardResponse> createPaymentCard(
            @PathVariable UUID userId,
            @Valid
            @RequestBody CreatePaymentCardRequest request) {
        PaymentCard paymentCard = service.create(userId, mapper.toPaymentCard(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toPaymentCardResponse(paymentCard));
    }

    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<PaymentCardResponse> findPaymentCard(@PathVariable UUID id) {
        PaymentCard paymentCard = service.findById(id);
        return ResponseEntity.ok(mapper.toPaymentCardResponse(paymentCard));
    }

    @GetMapping(value = "/users/me", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PaymentCardResponse>> findCurrentUserPaymentCards(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) Boolean active) {
        UUID userId = findUserId(authHeader);
        List<PaymentCardResponse> paymentCards = service.findByUserId(userId, active).stream()
                .map(mapper::toPaymentCardResponse)
                .toList();
        return ResponseEntity.ok(paymentCards);
    }

    @GetMapping(value = "/users/{userId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PaymentCardResponse>> findPaymentCardsByUserId(
            @PathVariable UUID userId,
            @RequestParam(required = false) Boolean active) {
        List<PaymentCardResponse> paymentCards = service.findByUserId(userId, active).stream()
                .map(mapper::toPaymentCardResponse)
                .toList();
        return ResponseEntity.ok(paymentCards);
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<PaymentCardResponse>> findPaymentCards(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            Pageable pageable) {
        Page<PaymentCard> userPage = service.findAll(firstName, lastName, pageable);
        return ResponseEntity.ok(userPage.map(mapper::toPaymentCardResponse));
    }

    @PutMapping(value = "/{id}/users/{userId}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<PaymentCardResponse> updatePaymentCard(
            @PathVariable UUID id,
            @PathVariable UUID userId,
            @Valid
            @RequestBody UpdatePaymentCardRequest request) {
        PaymentCard paymentCard = service.update(userId, mapper.toPaymentCard(id, request));
        return ResponseEntity.ok(mapper.toPaymentCardResponse(paymentCard));
    }

    @PatchMapping(value = "/{id}/status")
    public ResponseEntity<Void> updatePaymentCardStatus(
            @PathVariable UUID id, @RequestParam Boolean active) {
        UUID userId = userService.findUserIdByCardId(id);
        service.updateActiveStatus(id, userId, active);
        return ResponseEntity.noContent().build();
    }
}
