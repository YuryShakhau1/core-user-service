package by.shakhau.core.user.service.impl;

import by.shakhau.core.user.repository.PaymentCardRepository;
import by.shakhau.core.user.repository.UserRepository;
import by.shakhau.core.user.repository.entity.UserEntity;
import by.shakhau.core.user.service.exception.ResourceForbiddenException;
import by.shakhau.core.user.service.exception.ResourceNotFoundException;
import by.shakhau.core.user.service.mapper.UserMapper;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceImplTest extends CommonUtil {

    @Mock
    private UserMapper mapper;

    @Mock
    private UserRepository repository;

    @Mock
    private PaymentCardRepository paymentCardRepository;

    @InjectMocks
    private UserServiceImpl service;

    private User user;
    private UserEntity userEntity;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(USER_ID);
        user.setName(USER_NAME);
        user.setSurname(USER_SURNAME);

        userEntity = new UserEntity();
        userEntity.setId(USER_ID);
        userEntity.setName(USER_NAME);
        userEntity.setSurname(USER_SURNAME);
    }

    @Test
    void shouldCreateUserWhenUserIdValid() {
        var newUser = new User();
        var entityToSave = new UserEntity();

        when(repository.findIdByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(mapper.toEntity(newUser)).thenReturn(entityToSave);
        when(repository.save(entityToSave)).thenReturn(userEntity);
        when(mapper.toDomain(userEntity)).thenReturn(user);

        User result = service.create(newUser);

        assertThat(result).isEqualTo(user);

        verify(repository).findIdByEmail(user.getEmail());
        verify(mapper).toEntity(newUser);
        verify(repository).save(entityToSave);
        verify(mapper).toDomain(userEntity);
    }

    @Test
    void shouldUpdateUserWhenUserIdValidAndExists() {
        var newUser = new User();
        var entityToSave = new UserEntity();

        when(repository.findIdByEmail(user.getEmail())).thenReturn(Optional.of(USER_ID));
        when(repository.findById(USER_ID)).thenReturn(Optional.of(userEntity));
        when(repository.save(entityToSave)).thenReturn(userEntity);
        when(mapper.toDomain(userEntity)).thenReturn(user);

        User result = service.create(newUser);

        assertThat(result).isEqualTo(user);

        verify(mapper).updateEntity(user, userEntity);
        verify(repository).save(userEntity);
        verify(mapper).toDomain(userEntity);
    }

    @Test
    void shouldThrowResourceForbiddenExceptionWhenCreatingUserWithId() {
        var newUser = new User();
        newUser.setId(USER_ID);

        assertThatThrownBy(() -> service.create(newUser))
                .isInstanceOf(ResourceForbiddenException.class)
                .hasMessage("User id must be null");

        verify(repository, never()).save(any());
        verify(mapper, never()).toEntity(any());
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

        Page<User> result = service.findAll(USER_NAME, USER_SURNAME, pageable);

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
        service.updateActiveStatus(USER_ID, false);

        verify(paymentCardRepository).updateActiveStatusByUserId(USER_ID, false);
        verify(repository).updateActiveStatus(USER_ID, false);
    }

    @Test
    void shouldNotDeactivatePaymentCardsWhenUserBecomesActive() {
        service.updateActiveStatus(USER_ID, true);

        verify(paymentCardRepository, never()).updateActiveStatusByUserId(any(UUID.class), anyBoolean());

        verify(repository).updateActiveStatus(USER_ID, true);
    }
}
