package com.be.pos.backend_app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequest {

    private List<TransactionDetails> transactionDetails;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TransactionDetails {
        private BigDecimal price;
        private String name;
        private BigDecimal totalPrice;
        private Integer qty;
        private Long id;
    }
}
