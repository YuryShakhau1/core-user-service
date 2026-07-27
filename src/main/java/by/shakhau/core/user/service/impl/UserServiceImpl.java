package by.shakhau.core.user.service.impl;

import by.shakhau.core.user.messaging.event.UserCreatedEvent;
import by.shakhau.core.user.messaging.event.UserStatusUpdatedEvent;
import by.shakhau.core.user.messaging.mapper.UserEventMapper;
import by.shakhau.core.user.messaging.producer.CreateUserProducer;
import by.shakhau.core.user.messaging.producer.UpdateUserProducer;
import by.shakhau.core.user.messaging.producer.UpdateUserStatusProducer;
import by.shakhau.core.user.repository.PaymentCardRepository;
import by.shakhau.core.user.repository.UserRepository;
import by.shakhau.core.user.repository.entity.UserEntity;
import by.shakhau.core.user.repository.specification.UserSpecifications;
import by.shakhau.core.user.service.UserService;
import by.shakhau.core.user.service.exception.ResourceForbiddenException;
import by.shakhau.core.user.service.exception.ResourceNotFoundException;
import by.shakhau.core.user.service.mapper.UserMapper;
import by.shakhau.core.user.service.model.CreatedUser;
import by.shakhau.core.user.service.model.User;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.apache.commons.text.RandomStringGenerator;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UpdateUserStatusProducer updateUserStatusProducer;

    private final UserMapper mapper;
    private final UserRepository repository;
    private final UserEventMapper userEventMapper;

    private final CreateUserProducer createUserProducer;
    private final UpdateUserProducer updateUserProducer;

    private PaymentCardRepository paymentCardRepository;

    @Transactional
    @Override
    public User create(User user) {
        if (user.getId() != null) {
            if (repository.existsById(user.getId())) {
                throw new ResourceForbiddenException("User with ID %s already exists".formatted(user.getId()));
            }

            repository.insertUser(mapper.toEntity(user));
            return user;
        }

        if (repository.existsByEmail(user.getEmail())) {
            return mapper.toDomain(repository.findByEmail(user.getEmail()));
        }

        return mapper.toDomain(repository.save(mapper.toEntity(user)));
    }

    @Transactional
    @Override
    public CreatedUser createAndRegister(User user, String role) {
        User createdUser = create(user);

        StringBuilder tempPassword = generatePassword();
        UserCreatedEvent userCreatedEvent = userEventMapper.toUserCreatedEvent(createdUser);
        userCreatedEvent.setTempPassword(tempPassword);
        userCreatedEvent.setRole(role);
        createUserProducer.send(userCreatedEvent);

        return mapper.toCreatedUser(createdUser, tempPassword);
    }

    @Cacheable(value = "users", key = "#id")
    @Override
    public User findById(UUID id) {
        return mapper.toDomain(repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id = %s not found".formatted(id))));
    }

    @Override
    public UUID findUserIdByCardId(UUID cardId) {
        return repository.findUserIdByCardId(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found by %s card id".formatted(cardId)));
    }

    @Override
    public Page<User> findAll(String firstName, String lastName, Pageable pageable) {
        return repository.findAll(UserSpecifications.withFilters(firstName, lastName), pageable)
                .map(mapper::toDomain);
    }

    @Transactional
    @CachePut(value = "users", key = "#user.id")
    @Override
    public User update(User user) {
        if (user.getId() == null) {
            throw new ResourceForbiddenException("User id must not be null");
        }

        UserEntity userEntity = repository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User with id = %s not found".formatted(user.getId())));

        mapper.updateEntity(user, userEntity);

        userEntity = repository.save(userEntity);
        updateUserProducer.send(userEventMapper.toUserUpdatedEvent(userEntity));

        return mapper.toDomain(userEntity);
    }

    @Transactional
    @CacheEvict(value = "users", key = "#id")
    @Override
    public void updateActiveStatus(UUID id, boolean active) {
        if (!active) {
            paymentCardRepository.updateActiveStatusByUserId(id, active);
        }
        repository.updateActiveStatus(id, active);

        updateUserStatusProducer.send(new UserStatusUpdatedEvent(id, active));
    }

    private StringBuilder generatePassword() {
        RandomStringGenerator generator = new RandomStringGenerator.Builder()
                .withinRange('!', '~')
                .usingRandom(new SecureRandom()::nextInt)
                .build();

        return new StringBuilder(generator.generate(8));
    }
}
