package com.be.pos.backend_app.repository;

import com.be.pos.backend_app.entity.Transaction;
import com.be.pos.backend_app.projection.ReportProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long>, JpaSpecificationExecutor<Transaction> {

    @Query(value = "SELECT SUM(total) as total_transactions FROM transaction",nativeQuery = true)
    BigDecimal getTotalTransactions();

    @Query(value = """
        SELECT
        a.id AS transactionId,
        to_char(a.created_at,'YYYY-MM-DD HH24:MI') as createdAt,
        a.total,
        b.item_price as itemPrice,
        b.quantity,
        b.total_price as totalPrice,
        c.name as productName,
        a.transaction_code as transactionCode
        FROM TRANSACTION a JOIN transaction_details b
        ON a.transaction_code = b.transaction_code
        join product c on b.product = c.id;
    """,nativeQuery = true)
    List<ReportProjection> listReportTransactions();

    @Query(value = """
        SELECT
        a.id AS transactionId,
        to_char(a.created_at,'YYYY-MM-DD HH24:MI') as createdAt,
        a.total,
        b.item_price as itemPrice,
        b.quantity,
        b.total_price as totalPrice,
        c.name as productName,
        a.transaction_code as transactionCode
        FROM TRANSACTION a JOIN transaction_details b
        ON a.transaction_code = b.transaction_code
        join product c on b.product = c.id WHERE a.transaction_code=:transactionCode;
    """,nativeQuery = true)
    List<ReportProjection> listReportTransactionByTransactionCode(@Param("transactionCode")String transactionCode);
}
