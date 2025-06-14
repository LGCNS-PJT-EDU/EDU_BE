package com.education.takeit.admin.failLog.spec;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class FailLogSpecifications {

    public static <T> Specification<T> searchBy(
            String nickname,
            String email,
            String errorCode
    ) {
        return (root, query, cb) -> {
            // user 조인
            Join<Object,Object> user = root.join("user", JoinType.LEFT);

            List<Predicate> predicates = new ArrayList<>();
            // nickname 조건
            if (nickname != null && !nickname.isBlank()) {
                predicates.add(cb.like(
                        cb.lower(user.get("nickname")),
                        "%" + nickname.toLowerCase() + "%"
                ));
            }
            // email 조건
            if (email != null && !email.isBlank()) {
                predicates.add(cb.like(
                        cb.lower(user.get("email")),
                        "%" + email.toLowerCase() + "%"
                ));
            }
            // errorCode 조건
            if (errorCode != null && !errorCode.isBlank()) {
                predicates.add(cb.like(
                        cb.lower(root.get("errorCode")),
                        "%" + errorCode.toLowerCase() + "%"
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
