package by.shakhau.core.user.repository;

import by.shakhau.core.user.repository.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID>,
        JpaSpecificationExecutor<UserEntity> {

    @Query("SELECT pc.user.id FROM PaymentCardEntity pc WHERE pc.id = :cardId")
    Optional<UUID> findUserIdByCardId(UUID cardId);

    @Query("SELECT u.id FROM UserEntity u WHERE u.email = :email")
    Optional<UUID> findIdByEmail(String email);

    @Modifying
    @Query("UPDATE UserEntity u SET u.active = :active WHERE u.id = :id")
    void updateActiveStatus(UUID id, Boolean active);
}
