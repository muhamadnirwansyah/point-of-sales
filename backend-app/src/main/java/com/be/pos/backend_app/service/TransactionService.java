package com.be.pos.backend_app.service;

import com.be.pos.backend_app.model.SearchTransactionRequest;
import com.be.pos.backend_app.model.TransactionRequest;
import com.be.pos.backend_app.model.TransactionResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface TransactionService {

    TransactionResponse createTransaction(TransactionRequest transactionRequest);
    TransactionResponse findTransactionId(Long transactionId);
    List<Map<String,Object>> countSummary();
    Page<TransactionResponse> searchTransaction(SearchTransactionRequest searchTransactionRequest);
    byte[] generateTransactionReport();
    byte[] generateTransactionReportByTransactionCode(String transactionCode);
}
