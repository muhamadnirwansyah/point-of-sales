package com.be.pos.backend_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "transaction_code",nullable = false,unique = true)
    private String transactionCode;
    @Column(name = "created_at",nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "total",nullable = false)
    private BigDecimal total;
}
