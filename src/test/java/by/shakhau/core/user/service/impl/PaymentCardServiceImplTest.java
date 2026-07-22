package by.shakhau.core.user.service.impl;

import by.shakhau.core.user.repository.PaymentCardRepository;
import by.shakhau.core.user.repository.UserRepository;
import by.shakhau.core.user.repository.entity.PaymentCardEntity;
import by.shakhau.core.user.repository.entity.UserEntity;
import by.shakhau.core.user.service.exception.ResourceForbiddenException;
import by.shakhau.core.user.service.exception.ResourceNotFoundException;
import by.shakhau.core.user.service.mapper.PaymentCardMapper;
import by.shakhau.core.user.service.model.PaymentCard;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PaymentCardServiceImplTest extends CommonUtil {

    @Mock
    private PaymentCardMapper mapper;

    @Mock
    private PaymentCardRepository repository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PaymentCardServiceImpl service;

    private PaymentCard paymentCard;
    private PaymentCardEntity paymentCardEntity;
    private UserEntity userEntity;

    @BeforeEach
    public void setUp() {
        paymentCard = new PaymentCard();
        paymentCard.setId(CARD_ID);
        paymentCard.setNumber(CARD_NUMBER);
        paymentCard.setActive(true);

        paymentCardEntity = new PaymentCardEntity();
        paymentCardEntity.setId(CARD_ID);
        paymentCardEntity.setNumber(CARD_NUMBER);
        paymentCardEntity.setActive(true);

        userEntity = new UserEntity();
        userEntity.setId(USER_ID);
    }

    @Test
    void shouldCreatePaymentCardWhenPaymentCardIsValid() {
        var newCard = new PaymentCard();
        var newEntity = new PaymentCardEntity();

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(userEntity));
        when(mapper.toEntity(newCard)).thenReturn(newEntity);
        when(repository.save(newEntity)).thenReturn(paymentCardEntity);
        when(mapper.toDomain(paymentCardEntity)).thenReturn(paymentCard);

        PaymentCard result = service.create(USER_ID, newCard);

        assertThat(result).isEqualTo(paymentCard);
        assertThat(newEntity.getUser()).isEqualTo(userEntity);

        verify(repository).save(newEntity);
    }

    @Test
    void shouldThrowResourceForbiddenExceptionWhenCreatingPaymentCardWithId() {
        var card = new PaymentCard();
        card.setId(CARD_ID);

        assertThatThrownBy(() -> service.create(USER_ID, card))
                .isInstanceOf(ResourceForbiddenException.class)
                .hasMessage("Payment card id must be null");

        verify(repository, never()).save(any());
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenCreatingPaymentCardForUnknownUser() {
        var newCard = new PaymentCard();

        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(USER_ID, newCard))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User with id = %s not found".formatted(USER_ID));

        verify(repository, never()).save(any());
    }

    @Test
    void shouldReturnPaymentCardWhenCardExists() {
        when(repository.findById(CARD_ID)).thenReturn(Optional.of(paymentCardEntity));
        when(mapper.toDomain(paymentCardEntity)).thenReturn(paymentCard);

        PaymentCard result = service.findById(CARD_ID);

        assertThat(result).isEqualTo(paymentCard);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenCardDoesNotExist() {
        when(repository.findById(CARD_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(CARD_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Payment card with id = %s not found".formatted(CARD_ID));
    }

    @Test
    void shouldReturnActivePaymentCardsWhenActiveFilterIsSpecified() {
        when(repository.findAllByUserIdAndActive(USER_ID, true))
                .thenReturn(List.of(paymentCardEntity));

        when(mapper.toDomain(paymentCardEntity)).thenReturn(paymentCard);

        List<PaymentCard> result = service.findByUserId(USER_ID, true);

        assertThat(result).containsExactly(paymentCard);

        verify(repository).findAllByUserIdAndActive(USER_ID, true);
        verify(repository, never()).findAllByUserId(any(UUID.class));
    }

    @Test
    void shouldReturnAllPaymentCardsWhenActiveFilterIsNull() {
        when(repository.findAllByUserId(USER_ID)).thenReturn(List.of(paymentCardEntity));
        when(mapper.toDomain(paymentCardEntity)).thenReturn(paymentCard);

        List<PaymentCard> result = service.findByUserId(USER_ID, null);

        assertThat(result).containsExactly(paymentCard);

        verify(repository).findAllByUserId(USER_ID);
        verify(repository, never()).findAllByUserIdAndActive(any(UUID.class), anyBoolean());
    }

    @Test
    void shouldReturnPageOfPaymentCards() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<PaymentCardEntity> page = new PageImpl<>(List.of(paymentCardEntity));

        when(repository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(mapper.toDomain(paymentCardEntity)).thenReturn(paymentCard);

        Page<PaymentCard> result = service.findAll(USER_NAME, USER_NAME, pageable);

        assertThat(result.getContent()).containsExactly(paymentCard);
    }

    @Test
    void shouldUpdatePaymentCardWhenPaymentCardIsValid() {
        when(repository.findById(CARD_ID)).thenReturn(Optional.of(paymentCardEntity));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(userEntity));
        when(repository.save(paymentCardEntity)).thenReturn(paymentCardEntity);
        when(mapper.toDomain(paymentCardEntity)).thenReturn(paymentCard);

        PaymentCard result = service.update(USER_ID, paymentCard);

        assertThat(result).isEqualTo(paymentCard);

        verify(repository).save(paymentCardEntity);
        verify(mapper).updateEntity(paymentCard, paymentCardEntity);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenCardNotFound() {
        when(repository.findById(CARD_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(USER_ID, paymentCard))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Payment card with id = %s not found".formatted(CARD_ID));

        verify(mapper, never()).updateEntity(paymentCard, paymentCardEntity);
        verify(repository, never()).save(paymentCardEntity);
        verify(mapper, never()).toDomain(paymentCardEntity);
    }

    @Test
    void shouldThrowResourceForbiddenExceptionWhenUpdatingPaymentCardWithoutId() {
        var card = new PaymentCard();

        assertThatThrownBy(() -> service.update(USER_ID, card))
                .isInstanceOf(ResourceForbiddenException.class)
                .hasMessage("Payment card id must not be null");

        verify(repository, never()).save(any());
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenUpdatingPaymentCardForUnknownUser() {
        when(repository.findById(CARD_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(USER_ID, paymentCard))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Payment card with id = %s not found".formatted(CARD_ID));

        verify(repository, never()).save(any());
    }

    @Test
    void shouldUpdatePaymentCardActiveStatus() {
        service.updateActiveStatus(CARD_ID, USER_ID, false);

        verify(repository).updateActiveStatus(CARD_ID, false);
    }
}
