package by.shakhau.core.user.repository;

import by.shakhau.core.user.repository.entity.PaymentCardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface PaymentCardRepository extends JpaRepository<PaymentCardEntity, UUID>,
        JpaSpecificationExecutor<PaymentCardEntity> {

    List<PaymentCardEntity> findAllByUserId(UUID userId);

    List<PaymentCardEntity> findAllByUserIdAndActive(UUID userId, Boolean active);

    @Query(value = "SELECT pc.id FROM payment_cards pc WHERE pc.user_id = :userId AND pc.active = :active",
            nativeQuery = true)
    List<UUID> findIndicesByUserIdAndActive(UUID userId, Boolean active);

    @Query(value = "SELECT pc.id FROM payment_cards pc WHERE pc.user_id = :userId",
            nativeQuery = true)
    List<UUID> findIndicesByUserId(UUID userId);

    @Modifying
    @Query(
            value = "UPDATE payment_cards SET active = :active WHERE id = :id AND user_id = :userId",
            nativeQuery = true)
    void updateActiveStatus(UUID userId, UUID id, Boolean active);
}
