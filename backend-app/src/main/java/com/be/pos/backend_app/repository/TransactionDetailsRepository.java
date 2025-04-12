package com.be.pos.backend_app.repository;

import com.be.pos.backend_app.entity.TransactionDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionDetailsRepository extends JpaRepository<TransactionDetails,Long> {

    @Query(value = "SELECT * FROM transaction_details WHERE transaction_code=:transactionCode",nativeQuery = true)
    List<TransactionDetails> findTransactionDetailsByTransactionCode(@Param("transactionCode")String transactionCode);
}
