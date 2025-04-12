package com.be.pos.backend_app.specification;

import com.be.pos.backend_app.entity.Product;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

public class ProductSpecification {

    public static Specification<Product> inquiryByName(String name){
        return (root,query,criteriaBuilder) -> {
            if (Objects.isNull(name)){
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),"%"+name.toLowerCase()+"%");
        };
    }

    public static Specification<Product> inquiryByCategory(Long categoryId){
        return (root, query, criteriaBuilder) -> {
            if (Objects.isNull(categoryId)){
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("category").get("id"),categoryId);
        };
    }

    public static Specification<Product> inquiryProductActive(Integer deleted){
        return (root, query, criteriaBuilder) -> {
            if (Objects.isNull(deleted)){
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("deleted"),deleted);
        };
    }

    public static Specification<Product> inquiryByFromDateBetweenToDate(LocalDate fromDate, LocalDate toDate){
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            if (!Objects.isNull(fromDate)){
                LocalDateTime fromDateTime = fromDate.atStartOfDay();
                predicate = criteriaBuilder.and(predicate,criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"),fromDateTime));
            }

            if(!Objects.isNull(toDate)){
                LocalDateTime toDateTime = toDate.atTime(LocalTime.MAX);
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"),toDateTime));
            }
            return predicate;
        };
    }
}
