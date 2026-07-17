package by.shakhau.core.user.repository;

import by.shakhau.core.user.repository.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long>,
        JpaSpecificationExecutor<UserEntity> {

    @Query(value = "SELECT pc.user.id FROM PaymentCardEntity pc WHERE pc.id = :cardId")
    Optional<Long> findUserIdByCardId(Long cardId);

    @Modifying
    @Query("UPDATE UserEntity u SET u.active = :active WHERE u.id = :id")
    void updateActiveStatus(Long id, Boolean active);
}
