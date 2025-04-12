package com.be.pos.backend_app.service;

import com.be.pos.backend_app.model.ProductReport;
import com.be.pos.backend_app.model.ProductRequest;
import com.be.pos.backend_app.model.ProductResponse;
import com.be.pos.backend_app.model.SearchProductRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {

    ProductResponse save(ProductRequest request);
    ProductResponse update(ProductRequest request);
    ProductResponse findById(Long id);
    ProductResponse deleteById(Long id);
    Page<ProductResponse> findAll(SearchProductRequest request);
    byte[] generateReportPdf();
    byte[] generateReportExcel();
}
