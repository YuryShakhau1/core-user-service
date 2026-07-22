package by.shakhau.core.user.service.impl;

import by.shakhau.core.user.repository.PaymentCardRepository;
import by.shakhau.core.user.repository.UserRepository;
import by.shakhau.core.user.repository.entity.PaymentCardEntity;
import by.shakhau.core.user.repository.entity.UserEntity;
import by.shakhau.core.user.repository.specification.PaymentCardSpecifications;
import by.shakhau.core.user.service.PaymentCardService;
import by.shakhau.core.user.service.exception.ResourceForbiddenException;
import by.shakhau.core.user.service.exception.ResourceNotFoundException;
import by.shakhau.core.user.service.mapper.PaymentCardMapper;
import by.shakhau.core.user.service.model.PaymentCard;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PaymentCardServiceImpl implements PaymentCardService {

    private PaymentCardMapper mapper;
    private PaymentCardRepository repository;
    private UserRepository userRepository;

    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(value = "user-cards", key = "#userId + '-true'"),
            @CacheEvict(value = "user-cards", key = "#userId + '-false'"),
            @CacheEvict(value = "user-cards", key = "#userId + '-null'") })
    public PaymentCard create(Long userId, PaymentCard paymentCard) {
        if (paymentCard.getId() != null) {
            throw new ResourceForbiddenException("Payment card id must be null");
        }

        return save(userId, mapper.toEntity(paymentCard));
    }

    @Cacheable(value = "payment-cards", key = "#id")
    @Override
    public PaymentCard findById(Long id) {
        return mapper.toDomain(repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment card with id = %d not found".formatted(id))));
    }

    @Cacheable(value = "user-cards", key = "#userId + '-' + #active")
    @Override
    public List<PaymentCard> findByUserId(Long userId, Boolean active) {
        List<PaymentCardEntity> paymentCards = null;
        if (active != null) {
            paymentCards = repository.findAllByUserIdAndActive(userId, active);
        } else {
            paymentCards = repository.findAllByUserId(userId);
        }

        return paymentCards.stream()
                .map(pc -> mapper.toDomain(pc))
                .toList();
    }

    @Override
    public Page<PaymentCard> findAll(String name, String surname, Pageable pageable) {
        return repository.findAll(PaymentCardSpecifications.withFilters(name, surname), pageable)
                .map(u -> mapper.toDomain(u));
    }

    @CachePut(value = "payment-cards", key = "#paymentCard.id")
    @Caching(evict = {
            @CacheEvict(value = "user-cards", key = "#userId + '-true'"),
            @CacheEvict(value = "user-cards", key = "#userId + '-false'"),
            @CacheEvict(value = "user-cards", key = "#userId + '-null'") })
    @Transactional
    @Override
    public PaymentCard update(Long userId, PaymentCard paymentCard) {
        if (paymentCard.getId() == null) {
            throw new ResourceForbiddenException("Payment card id must not be null");
        }

        PaymentCardEntity paymentCardEntity = repository.findById(paymentCard.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment card with id = %d not found".formatted(paymentCard.getId())));

        mapper.updateEntity(paymentCard, paymentCardEntity);

        return save(userId, paymentCardEntity);
    }

    @Caching(evict = {
            @CacheEvict(value = "payment-cards", key = "#id"),
            @CacheEvict(value = "user-cards", key = "#userId + '-true'"),
            @CacheEvict(value = "user-cards", key = "#userId + '-false'"),
            @CacheEvict(value = "user-cards", key = "#userId + '-null'")
    })
    @Transactional
    @Override
    public void updateActiveStatus(Long userId, Long id, boolean active) {
        repository.updateActiveStatus(id, active);
    }

    private PaymentCard save(Long userId, PaymentCardEntity paymentCardEntity) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id = %d not found".formatted(userId)));
        paymentCardEntity.setUser(userEntity);
        return mapper.toDomain(repository.save(paymentCardEntity));
    }
}
