package com.be.pos.backend_app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionReportResponse {

    private Long transactionId;
    private String createdAt;
    private BigDecimal total;
    private BigDecimal itemPrice;
    private Integer quantity;
    private BigDecimal totalPrice;
    private String productName;
    private String transactionCode;

}
