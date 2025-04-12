package com.be.pos.backend_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "transaction_details")
public class TransactionDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "transaction_code",nullable = false)
    private String transactionCode;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product", nullable = false)
    private Product product;
    @Column(name = "quantity",nullable = false)
    private Integer quantity;
    @Column(name = "item_price",nullable = false)
    private BigDecimal itemPrice;
    @Column(name = "total_price",nullable = false)
    private BigDecimal totalPrice;
}
