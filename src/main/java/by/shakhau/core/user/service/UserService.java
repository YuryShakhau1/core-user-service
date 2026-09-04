package by.shakhau.core.user.service;

import by.shakhau.core.user.service.model.CreatedUser;
import by.shakhau.core.user.service.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {

    User create(User user);
    CreatedUser createAndRegister(User user, String role);
    User findById(UUID id);
    boolean existsById(UUID id);
    UUID findUserIdByCardId(UUID cardId);
    Page<User> findAll(String firstName, String lastName, Pageable pageable);
    User update(User user);
    void updateActiveStatus(UUID id, boolean active);
}
