package by.shakhau.core.user.repository;

import by.shakhau.core.user.repository.entity.PaymentCardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PaymentCardRepository extends JpaRepository<PaymentCardEntity, Long>,
        JpaSpecificationExecutor<PaymentCardEntity> {

    List<PaymentCardEntity> findAllByUserId(Long userId);

    @Query("SELECT pc FROM PaymentCardEntity pc JOIN FETCH pc.user WHERE pc.id = :id")
    Optional<PaymentCardEntity> findWithUser(Long id);

    @Modifying
    @Query("UPDATE PaymentCardEntity pc SET pc.active = :active WHERE pc.id = :id")
    void updateActiveStatus(Long id, Boolean active);

    @Modifying
    @Query("UPDATE PaymentCardEntity pc SET pc.active = :active WHERE pc.userId = :userId")
    void updateActiveStatusByUserId(Long userId, Boolean active);
}
