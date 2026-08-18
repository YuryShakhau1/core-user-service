package by.shakhau.core.user.controller;

import by.shakhau.core.user.controller.dto.request.CreatePaymentCardRequest;
import by.shakhau.core.user.controller.dto.request.UpdatePaymentCardRequest;
import by.shakhau.core.user.controller.dto.response.PaymentCardResponse;
import by.shakhau.core.user.controller.filter.AuthenticationFilter.UserPrincipal;
import by.shakhau.core.user.controller.mapper.PaymentCardDtoMapper;
import by.shakhau.core.user.service.PaymentCardService;
import by.shakhau.core.user.service.UserService;
import by.shakhau.core.user.service.model.PaymentCard;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/users/payment-cards")
@RequiredArgsConstructor
public class PaymentCardController {

    private final PaymentCardDtoMapper mapper;
    private final UserService userService;
    private final PaymentCardService service;

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<PaymentCardResponse> createPaymentCard(
            @RequestParam UUID userId,
            @Valid
            @RequestBody CreatePaymentCardRequest request) {
        PaymentCard paymentCard = service.create(userId, mapper.toPaymentCard(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toPaymentCardResponse(paymentCard));
    }

    @PostMapping(
            value = "/me", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<PaymentCardResponse> createCurrentUserPaymentCard(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid
            @RequestBody CreatePaymentCardRequest request) {
        PaymentCard paymentCard = service.create(principal.getId(), mapper.toPaymentCard(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toPaymentCardResponse(paymentCard));
    }

    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<PaymentCardResponse> findPaymentCard(
            @PathVariable UUID id, @RequestParam UUID userId) {
        PaymentCard paymentCard = service.findByIdAndUserId(id, userId);
        return ResponseEntity.ok(mapper.toPaymentCardResponse(paymentCard));
    }

    @GetMapping(value = "/me", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PaymentCardResponse>> findCurrentUserPaymentCards(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) Boolean active) {
        List<PaymentCardResponse> paymentCards = service.findByUserId(principal.getId(), active).stream()
                .map(mapper::toPaymentCardResponse)
                .toList();
        return ResponseEntity.ok(paymentCards);
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PaymentCardResponse>> findPaymentCardsByUserId(
            @RequestParam UUID userId,
            @RequestParam(required = false) Boolean active) {
        List<PaymentCardResponse> paymentCards = service.findByUserId(userId, active).stream()
                .map(mapper::toPaymentCardResponse)
                .toList();
        return ResponseEntity.ok(paymentCards);
    }

    @GetMapping(value = "/filtered", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<PaymentCardResponse>> findPaymentCards(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) Boolean active,
            Pageable pageable) {
        Page<PaymentCard> userPage = service.findAll(firstName, lastName, active, pageable);
        return ResponseEntity.ok(userPage.map(mapper::toPaymentCardResponse));
    }

    @PutMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<PaymentCardResponse> updatePaymentCard(
            @PathVariable UUID id,
            @RequestParam UUID userId,
            @Valid
            @RequestBody UpdatePaymentCardRequest request) {
        PaymentCard paymentCard = service.update(userId, mapper.toPaymentCard(id, request));
        return ResponseEntity.ok(mapper.toPaymentCardResponse(paymentCard));
    }

    @PutMapping(value = "/{id}/me", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<PaymentCardResponse> updateCurrentUserPaymentCard(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id,
            @Valid
            @RequestBody UpdatePaymentCardRequest request) {
        PaymentCard paymentCard = service.update(principal.getId(), mapper.toPaymentCard(id, request));
        return ResponseEntity.ok(mapper.toPaymentCardResponse(paymentCard));
    }

    @PatchMapping(value = "/{id}/me")
    public ResponseEntity<Void> updatePaymentCardStatus(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id,
            @RequestParam boolean active) {
        service.updateActiveStatus(principal.getId(), id, active);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/{id}")
    public ResponseEntity<Void> updateStatus(@PathVariable UUID id, @RequestParam boolean active) {
        UUID userId = userService.findUserIdByCardId(id);
        service.updateActiveStatus(userId, id, active);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deletePaymentCard(@PathVariable UUID id, @RequestParam UUID userId) {
        service.delete(userId, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/{id}/me")
    public ResponseEntity<Void> deleteCurrentUserPaymentCard(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id) {
        service.delete(principal.getId(), id);
        return ResponseEntity.noContent().build();
    }
}
