package com.be.pos.backend_app.repository;

import com.be.pos.backend_app.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long>, JpaSpecificationExecutor<Category> {

    @Query(value = "SELECT * FROM category WHERE deleted=0",nativeQuery = true)
    List<Category> listCategories();

    @Query(value = "SELECT COUNT(*) FROM category WHERE deleted=0",nativeQuery = true)
    Long countCategories();
}
