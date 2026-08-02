package by.shakhau.core.user.service.impl;

import by.shakhau.core.user.messaging.event.UserCreatedEvent;
import by.shakhau.core.user.messaging.event.UserStatusUpdatedEvent;
import by.shakhau.core.user.messaging.event.UserUpdatedEvent;
import by.shakhau.core.user.messaging.mapper.UserEventMapper;
import by.shakhau.core.user.messaging.producer.CreateUserProducer;
import by.shakhau.core.user.messaging.producer.UpdateUserProducer;
import by.shakhau.core.user.messaging.producer.UpdateUserStatusProducer;
import by.shakhau.core.user.repository.PaymentCardRepository;
import by.shakhau.core.user.repository.UserRepository;
import by.shakhau.core.user.repository.entity.UserEntity;
import by.shakhau.core.user.service.PaymentCardService;
import by.shakhau.core.user.service.exception.ResourceForbiddenException;
import by.shakhau.core.user.service.exception.ResourceNotFoundException;
import by.shakhau.core.user.service.mapper.UserMapper;
import by.shakhau.core.user.service.model.CreatedUser;
import by.shakhau.core.user.service.model.PaymentCard;
import by.shakhau.core.user.service.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceImplTest extends CommonTestUtil {

    @Mock
    private UpdateUserStatusProducer updateUserStatusProducer;

    @Mock
    private UserMapper mapper;

    @Mock
    private UserRepository repository;

    @Mock
    private UserEventMapper userEventMapper;

    @Mock
    private CreateUserProducer createUserProducer;

    @Mock
    private UpdateUserProducer updateUserProducer;

    @Mock
    private PaymentCardService paymentCardService;

    @InjectMocks
    private UserServiceImpl service;

    private User user;
    private UserEntity userEntity;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(USER_ID);
        user.setFirstName(USER_FIRST_NAME);
        user.setLastName(USER_LAST_NAME);

        userEntity = new UserEntity();
        userEntity.setId(USER_ID);
        userEntity.setName(USER_FIRST_NAME);
        userEntity.setSurname(USER_LAST_NAME);
    }

    @Test
    void shouldCreateAndRegisterUserWithIdWhenUserIdValid() {
        var newUser = new User();
        newUser.setId(USER_ID);
        var entityToSave = new UserEntity();
        var userCreatedEvent = new UserCreatedEvent();
        var createdUser = new CreatedUser();

        when(repository.existsById(USER_ID)).thenReturn(false);
        when(mapper.toEntity(newUser)).thenReturn(entityToSave);
        when(userEventMapper.toUserCreatedEvent(newUser)).thenReturn(userCreatedEvent);
        when(mapper.toCreatedUser(any(), any())).thenReturn(createdUser);

        CreatedUser result = service.createAndRegister(newUser, "ROLE_USER");

        assertThat(result).isEqualTo(createdUser);

        verify(createUserProducer).send(userCreatedEvent);
        verify(mapper).toEntity(newUser);
        verify(repository).insertUser(entityToSave);
        verify(mapper).toCreatedUser(any(), any());
    }

    @Test
    void shouldCreateUserWithIdWhenUserIdValid() {
        var newUser = new User();
        newUser.setId(USER_ID);
        var entityToSave = new UserEntity();

        when(repository.existsById(USER_ID)).thenReturn(false);
        when(mapper.toEntity(newUser)).thenReturn(entityToSave);

        User result = service.create(newUser);

        assertThat(result).isEqualTo(newUser);

        verify(mapper).toEntity(newUser);
        verify(repository).insertUser(entityToSave);
    }

    @Test
    void shouldCreateUserWithoutIdWhenUserIdValid() {
        var newUser = new User();
        var entityToSave = new UserEntity();

        when(repository.existsByEmail(user.getEmail())).thenReturn(false);
        when(mapper.toEntity(newUser)).thenReturn(entityToSave);
        when(repository.save(entityToSave)).thenReturn(userEntity);
        when(mapper.toDomain(userEntity)).thenReturn(user);

        User result = service.create(newUser);

        assertThat(result).isEqualTo(user);

        verify(repository).existsByEmail(user.getEmail());
        verify(mapper).toEntity(newUser);
        verify(repository).save(entityToSave);
        verify(mapper).toDomain(userEntity);
    }

    @Test
    void shouldUpdateUserWhenUserIdValidAndExists() {
        var newUser = new User();
        newUser.setId(USER_ID);
        var entityToSave = new UserEntity();
        var userUpdatedEvent = new UserUpdatedEvent();

        when(repository.findById(USER_ID)).thenReturn(Optional.of(userEntity));
        when(repository.save(entityToSave)).thenReturn(userEntity);
        when(mapper.toDomain(userEntity)).thenReturn(user);
        when(userEventMapper.toUserUpdatedEvent(userEntity)).thenReturn(userUpdatedEvent);

        User result = service.update(newUser);

        assertThat(result).isEqualTo(user);

        verify(mapper).updateEntity(user, userEntity);
        verify(repository).save(userEntity);
        verify(mapper).toDomain(userEntity);
        verify(updateUserProducer).send(userUpdatedEvent);
    }

    @Test
    void shouldReturnUserWhenUserExists() {
        when(repository.findById(USER_ID)).thenReturn(Optional.of(userEntity));
        when(mapper.toDomain(userEntity)).thenReturn(user);

        User result = service.findById(USER_ID);

        assertThat(result).isEqualTo(user);

        verify(repository).findById(USER_ID);
        verify(mapper).toDomain(userEntity);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenUserDoesNotExist() {
        when(repository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(USER_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User with id = %s not found".formatted(USER_ID));

        verify(repository).findById(USER_ID);
        verify(mapper, never()).toDomain(any());
    }

    @Test
    void shouldReturnUserIdWhenCardExists() {
        when(repository.findUserIdByCardId(CARD_ID)).thenReturn(Optional.of(USER_ID));

        UUID result = service.findUserIdByCardId(CARD_ID);

        assertThat(result).isEqualTo(USER_ID);

        verify(repository).findUserIdByCardId(CARD_ID);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenCardDoesNotExist() {
        when(repository.findUserIdByCardId(CARD_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findUserIdByCardId(CARD_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found by %s card id".formatted(CARD_ID));

        verify(repository).findUserIdByCardId(CARD_ID);
    }

    @Test
    void shouldReturnPageOfUsersWhenUsersExist() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<UserEntity> entityPage = new PageImpl<>(List.of(userEntity));

        when(repository.findAll(any(Specification.class), eq(pageable))).thenReturn(entityPage);
        when(mapper.toDomain(userEntity)).thenReturn(user);

        Page<User> result = service.findAll(USER_FIRST_NAME, USER_LAST_NAME, pageable);

        assertThat(result.getContent()).containsExactly(user);
        assertThat(result.getTotalElements()).isEqualTo(1);

        verify(repository).findAll(any(Specification.class), eq(pageable));
        verify(mapper).toDomain(userEntity);
    }

    @Test
    void shouldUpdateUserWhenUserIsValid() {
        when(repository.findById(USER_ID)).thenReturn(Optional.of(userEntity));
        when(repository.save(userEntity)).thenReturn(userEntity);
        when(mapper.toDomain(userEntity)).thenReturn(user);

        User result = service.update(user);

        assertThat(result).isEqualTo(user);

        verify(mapper).updateEntity(user, userEntity);
        verify(repository).save(userEntity);
        verify(mapper).toDomain(userEntity);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenUserNotFound() {
        when(repository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(user))
                .isInstanceOf(ResourceNotFoundException.class)
                        .hasMessage("User with id = %s not found".formatted(USER_ID));

        verify(mapper, never()).updateEntity(user, userEntity);
        verify(repository, never()).save(userEntity);
        verify(mapper, never()).toDomain(userEntity);
    }

    @Test
    void shouldThrowResourceForbiddenExceptionWhenUpdatingUserWithoutId() {
        var userWithoutId = new User();

        assertThatThrownBy(() -> service.update(userWithoutId))
                .isInstanceOf(ResourceForbiddenException.class)
                .hasMessage("User id must not be null");

        verify(repository, never()).save(any());
        verify(mapper, never()).toEntity(any());
    }

    @Test
    void shouldDeactivatePaymentCardsWhenUserBecomesInactive() {
        var paymentCardId = UUID.randomUUID();
        when(paymentCardService.findIndicesByUserId(USER_ID, true)).thenReturn(List.of(paymentCardId));

        service.updateActiveStatus(USER_ID, false);

        verify(paymentCardService).updateActiveStatus(USER_ID, paymentCardId, false);
        verify(repository).updateActiveStatus(USER_ID, false);
        verify(updateUserStatusProducer).send(new UserStatusUpdatedEvent(USER_ID, false));
    }

    @Test
    void shouldNotDeactivatePaymentCardsWhenUserBecomesActive() {
        service.updateActiveStatus(USER_ID, true);

        verify(paymentCardService, never()).updateActiveStatus(any(), any(), any(boolean.class));

        verify(repository).updateActiveStatus(USER_ID, true);
        verify(updateUserStatusProducer).send(new UserStatusUpdatedEvent(USER_ID, true));
    }
}
