package by.shakhau.core.user.service;

import by.shakhau.core.user.service.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    User create(User user);
    User findById(Long id);
    Long findUserIdByCardId(Long cardId);
    Page<User> findAll(String name, String surname, Pageable pageable);
    User update(User user);
    void updateActiveStatus(Long id, boolean active);
}
