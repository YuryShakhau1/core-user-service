package by.shakhau.core.user.service.impl;

import by.shakhau.core.user.repository.PaymentCardRepository;
import by.shakhau.core.user.repository.UserRepository;
import by.shakhau.core.user.repository.entity.UserEntity;
import by.shakhau.core.user.repository.specification.UserSpecifications;
import by.shakhau.core.user.service.UserService;
import by.shakhau.core.user.service.mapper.UserMapper;
import by.shakhau.core.user.service.model.User;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

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
            throw new IllegalArgumentException("User id must be null");
        }

        return mapper.toDomain(repository.save(mapper.toEntity(user)));
    }

    @Override
    public User findById(UUID id) {
        return mapper.toDomain(repository.findById(id).orElse(null));
    }

    @Override
    public UUID findUserIdByCardId(UUID cardId) {
        return repository.findUserIdByCardId(cardId)
                .orElseThrow(() -> new IllegalArgumentException("User not found by %s card id".formatted(cardId)));
    }

    @Override
    public Page<User> findAll(String name, String surname, Pageable pageable) {
        return repository.findAll(UserSpecifications.withFilters(name, surname), pageable)
                .map(u -> mapper.toDomain(u));
    }

    @Transactional
    @Override
    public User update(User user) {
        if (user.getId() == null) {
            throw new IllegalArgumentException("User id must not be null");
        }

        UserEntity userEntity = repository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("User with id = %s not found".formatted(user.getId())));

        mapper.updateEntity(user, userEntity);

        return mapper.toDomain(repository.save(userEntity));
    }

    @Transactional
    @Override
    public void updateActiveStatus(UUID id, boolean active) {
        if (!active) {
            paymentCardRepository.updateActiveStatusByUserId(id, active);
        }
        repository.updateActiveStatus(id, active);
    }
}
