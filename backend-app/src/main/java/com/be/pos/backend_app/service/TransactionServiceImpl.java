package com.be.pos.backend_app.service;

import com.be.pos.backend_app.entity.Product;
import com.be.pos.backend_app.entity.Transaction;
import com.be.pos.backend_app.entity.TransactionDetails;
import com.be.pos.backend_app.exception.ConflictException;
import com.be.pos.backend_app.exception.NotfoundException;
import com.be.pos.backend_app.model.*;
import com.be.pos.backend_app.projection.ReportProjection;
import com.be.pos.backend_app.repository.CategoryRepository;
import com.be.pos.backend_app.repository.ProductRepository;
import com.be.pos.backend_app.repository.TransactionDetailsRepository;
import com.be.pos.backend_app.repository.TransactionRepository;
import com.be.pos.backend_app.specification.TransactionSpecification;
import com.be.pos.backend_app.util.AppUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService{

    private final TransactionRepository transactionRepository;
    private final TransactionDetailsRepository transactionDetailsRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public TransactionResponse createTransaction(TransactionRequest transactionRequest) {
        log.info("process create transaction : [{}]",transactionRequest);
        if (transactionRequest.getTransactionDetails().isEmpty()){
            throw new ConflictException(HttpStatus.CONFLICT.value(),
                    Collections.singletonList("Transaction details cannot be empty !"));
        }
        var transactionCode = UUID.randomUUID().toString();
        List<TransactionDetails> transactionDetailsList = new ArrayList<>();
        transactionRequest.getTransactionDetails().forEach(transactionDetail -> {
            TransactionRequest.TransactionDetails modelTransactionDetails = new TransactionRequest.TransactionDetails();
            modelTransactionDetails.setId(transactionDetail.getId());
            modelTransactionDetails.setQty(transactionDetail.getQty());
            modelTransactionDetails.setTotalPrice(transactionDetail.getTotalPrice());
            modelTransactionDetails.setName(transactionDetail.getName());
            modelTransactionDetails.setPrice(transactionDetail.getPrice());

            TransactionDetails transactionDetails = new TransactionDetails();
            transactionDetails.setTransactionCode(transactionCode);
            transactionDetails.setTotalPrice(modelTransactionDetails.getTotalPrice());
            transactionDetails.setQuantity(modelTransactionDetails.getQty());
            transactionDetails.setItemPrice(modelTransactionDetails.getPrice());
            transactionDetails.setProduct(getProduct(modelTransactionDetails.getId()));

            if (transactionDetails.getProduct().getQuantity() <= transactionDetails.getQuantity()){
                throw new ConflictException(HttpStatus.CONFLICT.value(),
                        Collections.singletonList(String.format("Product %s unavailable stock !",transactionDetails.getProduct().getName())));
            }
            transactionDetailsList.add(transactionDetails);
        });

        Transaction transaction = new Transaction();
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setTransactionCode(transactionCode);
        BigDecimal grandTotal = transactionDetailsList.stream()
                        .map(TransactionDetails::getTotalPrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.info("grand total final : {}",grandTotal);
        transaction.setTotal(grandTotal);

        var transactionResponse = transactionRepository.save(transaction);
        var transactionDetailsResponseList = transactionDetailsRepository.saveAll(transactionDetailsList);

        log.info("process deduct product..");
        transactionDetailsResponseList.forEach(transactionDetailsDeductProduct -> deductProduct(transactionDetailsDeductProduct.getProduct().getId(),transactionDetailsDeductProduct.getQuantity()));
        return TransactionResponse.builder()
                .transactionId(transactionResponse.getId())
                .transactionCode(transactionCode)
                .total(AppUtil.formatRupiah(grandTotal))
                .createdAt(AppUtil.formatDateTime(transactionResponse.getCreatedAt()))
                .transactionDetailsResponse(transactionDetailsList.stream().map(transactionDetailMap ->
                        TransactionResponse.TransactionDetailsResponse.builder()
                                .transactionCode(transactionDetailMap.getTransactionCode())
                                .itemPrice(AppUtil.formatRupiah(transactionDetailMap.getItemPrice()))
                                .quantity(transactionDetailMap.getQuantity())
                                .totalPrice(AppUtil.formatRupiah(transactionDetailMap.getTotalPrice()))
                                .productResponse(ProductResponse.builder()
                                        .id(transactionDetailMap.getProduct().getId())
                                        .name(transactionDetailMap.getProduct().getName())
                                        .build())
                                .build()).toList()).build();
    }

    private Product getProduct(Long productId){
        return productRepository.findById(productId)
                .orElseThrow(() -> new NotfoundException(HttpStatus.NOT_FOUND.value(),
                        Collections.singletonList("Product ID not found !")));
    }


    private void deductProduct(Long productId, Integer quantity){
        log.info("process deduct product : {} - quantity : {}",productId, quantity);
        var currentProduct = productRepository.findById(productId)
                .orElseThrow(() -> new NotfoundException(HttpStatus.NOT_FOUND.value(),
                        Collections.singletonList("Product ID not found !")));
        currentProduct.setQuantity(currentProduct.getQuantity() - quantity);
        productRepository.save(currentProduct);
    }

    @Override
    public TransactionResponse findTransactionId(Long transactionId) {
        log.info("process find transaction by id : {}",transactionId);
        return transactionRepository.findById(transactionId)
                .map(transaction -> TransactionResponse.builder()
                        .transactionId(transactionId)
                        .total(AppUtil.formatRupiah(transaction.getTotal()))
                        .createdAt(AppUtil.formatDateTime(transaction.getCreatedAt()))
                        .transactionCode(transaction.getTransactionCode())
                        .transactionDetailsResponse(transactionDetailsRepository
                                .findTransactionDetailsByTransactionCode(transaction.getTransactionCode())
                                .stream().map(transactionDetails ->
                                        TransactionResponse
                                                .TransactionDetailsResponse
                                                .builder()
                                                .transactionCode(transactionDetails.getTransactionCode())
                                                .quantity(transactionDetails.getQuantity())
                                                .totalPrice(AppUtil.formatRupiah(transactionDetails.getTotalPrice()))
                                                .itemPrice(AppUtil.formatRupiah(transactionDetails.getItemPrice()))
                                                .productResponse(ProductResponse.builder()
                                                        .id(transactionDetails.getProduct().getId())
                                                        .name(transactionDetails.getProduct().getName())
                                                        .build())
                                                .build())
                                .toList())
                        .build())
                .orElseThrow(() -> new NotfoundException(HttpStatus.NOT_FOUND.value(),
                        Collections.singletonList("Transaction ID not found !")));
    }

    @Override
    public List<Map<String, Object>> countSummary() {
        BigDecimal grandTotal = transactionRepository.getTotalTransactions();
        Long totalQuantity = productRepository.getTotalProduct();
        Long totalCategory = categoryRepository.countCategories();
        Map<String,Object> summaryMap = new HashMap<>();
        summaryMap.put("totalProduct", totalQuantity);
        summaryMap.put("grandTotal", AppUtil.formatRupiah(grandTotal));
        summaryMap.put("totalCategory", totalCategory);
        return List.of(summaryMap);
    }

    @Override
    public Page<TransactionResponse> searchTransaction(SearchTransactionRequest searchTransactionRequest) {
        log.info("process searching transaction : {}",searchTransactionRequest);

        Specification<Transaction> transactionSpecification = Specification.where(null);
        if (!searchTransactionRequest.getFromDate().isEmpty() && !searchTransactionRequest.getToDate().isEmpty()){
            LocalDate fromDate = LocalDate.parse(searchTransactionRequest.getFromDate());
            LocalDate toDate = LocalDate.parse(searchTransactionRequest.getToDate());
            transactionSpecification = transactionSpecification.and(TransactionSpecification
                    .inquiryByFromDateToDate(fromDate, toDate));
        }

        Page<Transaction> transactionPage = transactionRepository.findAll(transactionSpecification,
                PageRequest.of(searchTransactionRequest.getPage(), searchTransactionRequest.getSize(),
                        Sort.by(Sort.Direction.DESC, "id")));

        return transactionPage.map(transaction -> TransactionResponse.builder()
                .transactionId(transaction.getId())
                .transactionCode(transaction.getTransactionCode())
                .total(AppUtil.formatRupiah(transaction.getTotal()))
                .createdAt(AppUtil.formatDateTime(transaction.getCreatedAt()))
                .build());
    }

    @Override
    public byte[] generateTransactionReport() {
        log.info("process generate transaction report..");
        List<ReportProjection> reportProjections = transactionRepository.listReportTransactions();
        if (reportProjections.isEmpty()){
            throw new NotfoundException(HttpStatus.NOT_FOUND.value(),
                    Collections.singletonList("Report transaction is empty !"));
        }
         List<TransactionReportResponse> responseTransactionSources = reportProjections.stream().map(transactionReport -> TransactionReportResponse.builder()
                .transactionCode(transactionReport.getTransactioncode())
                .createdAt(transactionReport.getCreatedat())
                .total(transactionReport.getTotal())
                .transactionId(transactionReport.getTransactionid())
                .totalPrice(transactionReport.getTotalprice())
                .quantity(transactionReport.getQuantity())
                .itemPrice(transactionReport.getItemprice())
                .productName(transactionReport.getProductname())
                .build()).toList();
        try{
            InputStream inputStream = getClass().getResourceAsStream("/reports/transaction_report.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(responseTransactionSources);
            Map<String,Object> parameters = new HashMap<>();
            parameters.put("generatedBy", "Admin"); // by active token
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            log.info("process generate transaction report already finish..");
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (Exception e) {
            log.error("Error because : {}",e.getMessage());
            throw new ConflictException(HttpStatus.CONFLICT.value(),
                    Collections.singletonList("Transaction report failed to generate !"));
        }
    }

    @Override
    public byte[] generateTransactionReportByTransactionCode(String transactionCode) {
        log.info("process generate transaction by transaction code : {} report..",transactionCode);
        List<ReportProjection> reportProjections = transactionRepository.listReportTransactionByTransactionCode(transactionCode);
        if (reportProjections.isEmpty()){
            throw new NotfoundException(HttpStatus.NOT_FOUND.value(),
                    Collections.singletonList("Report transaction is empty !"));
        }
        List<TransactionReportResponse> responseTransactionSources = reportProjections.stream().map(transactionReport -> TransactionReportResponse.builder()
                .transactionCode(transactionReport.getTransactioncode())
                .createdAt(transactionReport.getCreatedat())
                .total(transactionReport.getTotal())
                .transactionId(transactionReport.getTransactionid())
                .totalPrice(transactionReport.getTotalprice())
                .quantity(transactionReport.getQuantity())
                .itemPrice(transactionReport.getItemprice())
                .productName(transactionReport.getProductname())
                .build()).toList();
        try{
            InputStream inputStream = getClass().getResourceAsStream("/reports/transaction_report.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(responseTransactionSources);
            Map<String,Object> parameters = new HashMap<>();
            parameters.put("generatedBy", "Admin"); // by active token
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            log.info("process generate transaction report by transaction code already finish..");
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (Exception e) {
            log.error("Error because : {}",e.getMessage());
            throw new ConflictException(HttpStatus.CONFLICT.value(),
                    Collections.singletonList("Transaction report by transaction code failed to generate !"));
        }
    }
}
