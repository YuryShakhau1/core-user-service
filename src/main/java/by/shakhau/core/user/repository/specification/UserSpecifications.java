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

    public static Specification<UserEntity> withFilters(String name, String surname) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (name != null && !name.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("name")), name.toLowerCase() + "%"));
            }

            if (surname != null && !surname.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("surname")), surname.toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
