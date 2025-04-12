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
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name",nullable = false)
    private String name;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    @Column(name = "price", nullable = false)
    private BigDecimal price;
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    @Column(name = "deleted",nullable = false)
    private Integer deleted;
    @Column(name = "created_at",nullable = false)
    private LocalDateTime createdAt;
}
