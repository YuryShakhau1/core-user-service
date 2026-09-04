package by.shakhau.core.user.repository.specification;

import by.shakhau.core.user.repository.entity.UserEntity;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserSpecifications {

    public static Specification<UserEntity> withFilters(String firstName, String lastName) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (firstName != null && !firstName.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("name")), firstName.toLowerCase() + "%"));
            }

            if (lastName != null && !lastName.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("surname")), lastName.toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
