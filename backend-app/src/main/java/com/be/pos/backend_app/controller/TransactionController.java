package com.be.pos.backend_app.controller;

import com.be.pos.backend_app.model.ApiResponse;
import com.be.pos.backend_app.model.SearchTransactionRequest;
import com.be.pos.backend_app.model.TransactionRequest;
import com.be.pos.backend_app.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/transaction")
@RequiredArgsConstructor
@Tag(name = "Transaction Controller", description = "This is api for management related transaction.")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping(value = "/checkout")
    @Operation(summary = "Checkout transaction", description = "For simulation you should have Bearer <Token>")
    public ResponseEntity<ApiResponse> checkout(@RequestBody TransactionRequest request){
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.isOk(transactionService.createTransaction(request)));
    }

    @GetMapping(value = "/summary")
    @Operation(summary = "Summary of transaction", description = "For simulation you should have Bearer <Token>")
    public ResponseEntity<ApiResponse> summary(){
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.isOk(transactionService.countSummary()));
    }

    @GetMapping(value = "/findbyid/{id}")
    @Operation(summary = "Find transaction byd id of transaction", description = "For simulation you should have Bearer <Token>")
    public ResponseEntity<ApiResponse> findById(@PathVariable("id")Long id){
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.isOk(transactionService.findTransactionId(id)));
    }

    @GetMapping(value = "/search")
    @Operation(summary = "Search transaction existing", description = "For simulation you should have Bearer <Token>")
    public ResponseEntity<ApiResponse> search(SearchTransactionRequest request){
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.isOk(transactionService.searchTransaction(request)));
    }

    @GetMapping(value = "/report-pdf")
    @Operation(summary = "Reports pdf transaction existing", description = "For simulation you should have Bearer <Token>")
    public ResponseEntity<byte[]> generatePdf(){
        byte[] pdfBytes = transactionService.generateTransactionReport();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transaction_report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping(value = "/report/{transactionCode}/pdf")
    @Operation(summary = "Reports pdf transaction code existing", description = "For simulation you should have Bearer <Token>")
    public ResponseEntity<byte[]> generatePdfByTransactionCode(@PathVariable("transactionCode")String transactionCode){
        byte[] pdfBytes = transactionService.generateTransactionReportByTransactionCode(transactionCode);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transaction_"+transactionCode+"_report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
