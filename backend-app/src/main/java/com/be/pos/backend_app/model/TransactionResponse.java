package com.be.pos.backend_app.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionResponse {

    private Long transactionId;
    private String createdAt;
    private String total;
    private String transactionCode;
    private List<TransactionDetailsResponse> transactionDetailsResponse;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class TransactionDetailsResponse {
        private String itemPrice;
        private Integer quantity;
        private String totalPrice;
        private String transactionCode;
        private ProductResponse productResponse;
    }
}
