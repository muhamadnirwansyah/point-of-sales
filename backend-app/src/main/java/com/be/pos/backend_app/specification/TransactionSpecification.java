package com.be.pos.backend_app.specification;

import com.be.pos.backend_app.entity.Transaction;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

public class TransactionSpecification {

    public static Specification<Transaction> inquiryByFromDateToDate(LocalDate fromDate, LocalDate toDate){
        return (root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            if (!Objects.isNull(fromDate)){
                LocalDateTime fromDateTime = fromDate.atStartOfDay();
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"),fromDateTime));
            }

            if (!Objects.isNull(toDate)){
                LocalDateTime toDateTime = toDate.atTime(LocalTime.MAX);
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"),toDateTime));
            }
            return predicate;
        };
    }
}
