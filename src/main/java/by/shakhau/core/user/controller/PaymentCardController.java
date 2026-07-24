package by.shakhau.core.user.controller;

import by.shakhau.core.user.controller.dto.response.GetPaymentCardResponse;
import by.shakhau.core.user.controller.dto.resuest.CreatePaymentCardRequest;
import by.shakhau.core.user.controller.dto.resuest.UpdatePaymentCardRequest;
import by.shakhau.core.user.controller.mapper.PaymentCardDtoMapper;
import by.shakhau.core.user.service.PaymentCardService;
import by.shakhau.core.user.service.UserService;
import by.shakhau.core.user.service.model.PaymentCard;
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

import java.util.List;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/payment-card")
@AllArgsConstructor
public class PaymentCardController {

    private PaymentCardDtoMapper mapper;
    private UserService userService;
    private PaymentCardService service;

    @PostMapping(value = "/users/{userId}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<GetPaymentCardResponse> createPaymentCard(
            @PathVariable UUID userId,
            @Valid
            @RequestBody CreatePaymentCardRequest request) {
        PaymentCard paymentCard = service.create(userId, mapper.toPaymentCard(request));
        return ResponseEntity.ok(mapper.toGetPaymentCardResponse(paymentCard));
    }

    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<GetPaymentCardResponse> findPaymentCard(@PathVariable UUID id) {
        PaymentCard paymentCard = service.findById(id);
        return ResponseEntity.ok(mapper.toGetPaymentCardResponse(paymentCard));
    }

    @GetMapping(value = "/users/{userId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GetPaymentCardResponse>> findPaymentCardByUserId(
            @PathVariable UUID userId,
            @RequestParam(required = false) Boolean active) {
        List<GetPaymentCardResponse> paymentCards = service.findByUserId(userId, active).stream()
                .map(mapper::toGetPaymentCardResponse)
                .toList();
        return ResponseEntity.ok(paymentCards);
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<GetPaymentCardResponse>> findPaymentCards(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String surname,
            Pageable pageable) {
        Page<PaymentCard> userPage = service.findAll(name, surname, pageable);
        return ResponseEntity.ok(userPage.map(mapper::toGetPaymentCardResponse));
    }

    @PutMapping(value = "/{id}/users/{userId}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<GetPaymentCardResponse> updatePaymentCard(
            @PathVariable UUID id,
            @PathVariable UUID userId,
            @Valid
            @RequestBody UpdatePaymentCardRequest request) {
        PaymentCard paymentCard = service.update(userId, mapper.toPaymentCard(id, request));
        return ResponseEntity.ok(mapper.toGetPaymentCardResponse(paymentCard));
    }

    @PatchMapping(value = "/{id}/status")
    public ResponseEntity<Void> updatePaymentCardStatus(
            @PathVariable UUID id, @RequestParam Boolean active) {
        UUID userId = userService.findUserIdByCardId(id);
        service.updateActiveStatus(id, userId, active);
        return ResponseEntity.noContent().build();
    }
}
