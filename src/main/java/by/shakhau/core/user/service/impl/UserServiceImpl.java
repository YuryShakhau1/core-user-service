package by.shakhau.core.user.service.impl;

import by.shakhau.core.user.repository.PaymentCardRepository;
import by.shakhau.core.user.repository.UserRepository;
import by.shakhau.core.user.repository.specification.UserSpecifications;
import by.shakhau.core.user.service.UserService;
import by.shakhau.core.user.service.exception.ResourceForbiddenException;
import by.shakhau.core.user.service.exception.ResourceNotFoundException;
import by.shakhau.core.user.service.mapper.UserMapper;
import by.shakhau.core.user.service.model.User;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserMapper mapper;
    private UserRepository repository;

    private PaymentCardRepository paymentCardRepository;

    @Transactional
    @Override
    public User create(User user) {
        if (user.getId() != null) {
            throw new ResourceForbiddenException("User id must be null");
        }

        return mapper.toDomain(repository.save(mapper.toEntity(user)));
    }

    @Cacheable(value = "users", key = "#id")
    @Override
    public User findById(Long id) {
        return mapper.toDomain(repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id = %d not found".formatted(id))));
    }

    @Override
    public Long findUserIdByCardId(Long cardId) {
        return repository.findUserIdByCardId(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found by %d card id".formatted(cardId)));
    }

    @Override
    public Page<User> findAll(String name, String surname, Pageable pageable) {
        return repository.findAll(UserSpecifications.withFilters(name, surname), pageable)
                .map(u -> mapper.toDomain(u));
    }

    @Transactional
    @CachePut(value = "users", key = "#user.id")
    @Override
    public User update(User user) {
        if (user.getId() == null) {
            throw new ResourceForbiddenException("User id must not be null");
        }

        return mapper.toDomain(repository.save(mapper.toEntity(user)));
    }

    @Transactional
    @CacheEvict(value = "users", key = "#id")
    @Override
    public void updateActiveStatus(Long id, boolean active) {
        if (!active) {
            paymentCardRepository.updateActiveStatusByUserId(id, active);
        }
        repository.updateActiveStatus(id, active);
    }
}
