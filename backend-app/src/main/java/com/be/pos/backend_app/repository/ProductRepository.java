package com.be.pos.backend_app.repository;

import com.be.pos.backend_app.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long>, JpaSpecificationExecutor<Product> {

    @Query(value = "SELECT SUM(quantity) as total_product FROM product WHERE deleted=0",nativeQuery = true)
    Long getTotalProduct();
}
