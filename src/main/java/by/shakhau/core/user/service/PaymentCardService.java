package by.shakhau.core.user.service;

import by.shakhau.core.user.service.model.PaymentCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface PaymentCardService {

    PaymentCard create(UUID userId, PaymentCard paymentCard);
    PaymentCard findByIdAndUserId(UUID id, UUID userId);
    List<UUID> findIndicesByUserId(UUID userId, Boolean active);
    List<PaymentCard> findByUserId(UUID userId, Boolean active);
    Page<PaymentCard> findAll(String firstName, String lastName, Pageable pageable);
    PaymentCard update(UUID userId, PaymentCard paymentCard);
    void updateActiveStatus(UUID userId, UUID id, boolean active);
}
