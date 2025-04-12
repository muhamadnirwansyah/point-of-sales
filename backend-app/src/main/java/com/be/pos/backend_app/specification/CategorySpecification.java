package com.be.pos.backend_app.specification;

import com.be.pos.backend_app.entity.Category;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

public class CategorySpecification {

    public static Specification<Category> inquiryByActiveItem(Integer deleted){
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (Objects.isNull(deleted)){
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("deleted"),deleted);
        };
    }
}
