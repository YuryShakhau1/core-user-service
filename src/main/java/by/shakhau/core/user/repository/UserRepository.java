package by.shakhau.core.user.repository;

import by.shakhau.core.user.repository.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<UserEntity, Long>,
        JpaSpecificationExecutor<UserEntity> {

    @Modifying
    @Query("UPDATE UserEntity u SET u.active = :active WHERE u.id = :id")
    void updateActiveStatus(Long id, Boolean active);
}
