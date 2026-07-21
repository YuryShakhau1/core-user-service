package by.shakhau.core.user.service;

import by.shakhau.core.user.service.model.PaymentCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PaymentCardService {

    PaymentCard create(Long userId, PaymentCard paymentCard);
    PaymentCard findById(Long id);
    List<PaymentCard> findByUserId(Long userId, Boolean active);
    Page<PaymentCard> findAll(String name, String surname, Pageable pageable);
    PaymentCard update(Long userId, PaymentCard paymentCard);
    void updateActiveStatus(Long id, boolean active);
}
