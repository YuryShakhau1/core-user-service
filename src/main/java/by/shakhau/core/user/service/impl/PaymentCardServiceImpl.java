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
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentCardServiceImpl implements PaymentCardService {

    private final PaymentCardMapper mapper;
    private final PaymentCardRepository repository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(value = "user-cards", key = "#userId + '-true'"),
            @CacheEvict(value = "user-cards", key = "#userId + '-false'"),
            @CacheEvict(value = "user-cards", key = "#userId + '-null'") })
    public PaymentCard create(UUID userId, PaymentCard paymentCard) {
        if (paymentCard.getId() != null) {
            throw new ResourceForbiddenException("Payment card id must be null");
        }

        return save(userId, mapper.toEntity(paymentCard));
    }

    @Cacheable(value = "payment-cards", key = "#id")
    @Override
    public PaymentCard findByIdAndUserId(UUID id, UUID userId) {
        return mapper.toDomain(repository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment card with id = %s not found".formatted(id))));
    }

    @Cacheable(value = "user-cards", key = "#userId + '-' + #active")
    @Override
    public List<UUID> findIndicesByUserId(UUID userId, Boolean active) {
        if (active != null) {
            return repository.findIndicesByUserIdAndActive(userId, active);
        }

        return repository.findIndicesByUserId(userId);
    }

    @Cacheable(value = "user-cards", key = "#userId + '-' + #active")
    @Override
    public List<PaymentCard> findByUserId(UUID userId, Boolean active) {
        List<PaymentCardEntity> paymentCards = null;
        if (active != null) {
            paymentCards = repository.findAllByUserIdAndActive(userId, active);
        } else {
            paymentCards = repository.findAllByUserId(userId);
        }

        return paymentCards.stream()
                .map(pc -> mapper.toDomain(pc))
                .collect(Collectors.toList());
    }

    @Override
    public Page<PaymentCard> findAll(String firstName, String lastName, Boolean active, Pageable pageable) {
        return repository.findAll(PaymentCardSpecifications.withFilters(firstName, lastName, active), pageable)
                .map(mapper::toDomain);
    }

    @CachePut(value = "payment-cards", key = "#paymentCard.id")
    @Caching(evict = {
            @CacheEvict(value = "user-cards", key = "#userId + '-true'"),
            @CacheEvict(value = "user-cards", key = "#userId + '-false'"),
            @CacheEvict(value = "user-cards", key = "#userId + '-null'") })
    @Transactional
    @Override
    public PaymentCard update(UUID userId, PaymentCard paymentCard) {
        if (paymentCard.getId() == null) {
            throw new ResourceForbiddenException("Payment card id must not be null");
        }

        PaymentCardEntity paymentCardEntity = repository.findById(paymentCard.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment card with id = %s not found".formatted(paymentCard.getId())));

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
    public void updateActiveStatus(UUID userId, UUID id, boolean active) {
        repository.updateActiveStatus(userId, id, active);
    }

    @Transactional
    @Override
    public void delete(UUID userId, UUID id) {
        repository.deleteByUserIdAndId(userId, id);
    }

    private PaymentCard save(UUID userId, PaymentCardEntity paymentCardEntity) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id = %s not found".formatted(userId)));
        paymentCardEntity.setUser(userEntity);
        return mapper.toDomain(repository.save(paymentCardEntity));
    }
}
