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

    @Modifying
    @Query("UPDATE PaymentCardEntity pc SET pc.active = :active WHERE pc.id = :id")
    void updateActiveStatus(Long id, Boolean active);

    @Modifying
    @Query("UPDATE PaymentCardEntity pc SET pc.active = :active WHERE pc.user.id = :userId")
    void updateActiveStatusByUserId(Long userId, Boolean active);
}
