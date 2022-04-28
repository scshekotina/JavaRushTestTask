package com.game.repository;

import com.game.entity.Player;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.Date;

public class PlayerSpecification implements Specification<Player> {

    private SearchCriteria criteria;

    public PlayerSpecification(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<Player> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

        if (criteria.getOperation().equalsIgnoreCase(">=")) {
            if (root.get(criteria.getKey()).getJavaType() == Date.class) {
                return criteriaBuilder.greaterThanOrEqualTo(
                        root.<Date>get(criteria.getKey()), (Date)criteria.getValue());
            } else {
                return criteriaBuilder.greaterThanOrEqualTo(
                        root.<String>get(criteria.getKey()), criteria.getValue().toString());
            }
        }
        else if (criteria.getOperation().equalsIgnoreCase("<=")) {
            if (root.get(criteria.getKey()).getJavaType() == Date.class) {
                return criteriaBuilder.lessThanOrEqualTo(
                        root.<Date>get(criteria.getKey()), (Date) criteria.getValue());
            } else {
                return criteriaBuilder.lessThanOrEqualTo(
                        root.<String>get(criteria.getKey()), criteria.getValue().toString());
            }
        }
        else if (criteria.getOperation().equalsIgnoreCase(":")) {
            if (root.get(criteria.getKey()).getJavaType() == String.class) {
                return criteriaBuilder.like(
                        root.<String>get(criteria.getKey()), "%" + criteria.getValue() + "%");
            } else {
                return criteriaBuilder.equal(root.get(criteria.getKey()), criteria.getValue());
            }
        }
        return null;
    }
}
