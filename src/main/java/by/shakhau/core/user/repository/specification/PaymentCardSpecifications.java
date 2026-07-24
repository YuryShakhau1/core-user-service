package by.shakhau.core.user.repository.specification;

import by.shakhau.core.user.repository.entity.PaymentCardEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class PaymentCardSpecifications {

    public static Specification<PaymentCardEntity> withFilters(String name, String surname) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (name != null && !name.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("user").get("name")), name.toLowerCase() + "%"));
            }

            if (surname != null && !surname.isBlank()) {
                return cb.like(cb.lower(root.get("user").get("surname")), surname.toLowerCase() + "%");
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<PaymentCardEntity> hasUserSurname(String surname) {
        return (root, query, cb) -> {
            if (surname == null || surname.isBlank()) {
                return cb.conjunction();
            }

            return cb.like(cb.lower(root.get("user").get("surname")), "%" + surname.toLowerCase() + "%");
        };
    }
}
