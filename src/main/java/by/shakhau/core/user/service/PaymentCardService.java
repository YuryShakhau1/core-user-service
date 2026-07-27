package by.shakhau.core.user.service;

import by.shakhau.core.user.service.model.PaymentCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface PaymentCardService {

    PaymentCard create(UUID userId, PaymentCard paymentCard);
    PaymentCard findById(UUID id);
    List<PaymentCard> findByUserId(UUID userId, Boolean active);
    Page<PaymentCard> findAll(String firstName, String lastName, Pageable pageable);
    PaymentCard update(UUID userId, PaymentCard paymentCard);
    void updateActiveStatus(UUID id, UUID userId, boolean active);
}
