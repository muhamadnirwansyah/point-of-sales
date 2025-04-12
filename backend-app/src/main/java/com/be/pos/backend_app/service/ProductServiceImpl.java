package com.be.pos.backend_app.service;

import com.be.pos.backend_app.entity.Product;
import com.be.pos.backend_app.exception.ConflictException;
import com.be.pos.backend_app.exception.NotfoundException;
import com.be.pos.backend_app.model.*;
import com.be.pos.backend_app.repository.CategoryRepository;
import com.be.pos.backend_app.repository.ProductRepository;
import com.be.pos.backend_app.specification.ProductSpecification;
import com.be.pos.backend_app.util.AppUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsReportConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public ProductResponse save(ProductRequest request) {
        log.info("process save product : {}",request);
        var findCategory = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new NotfoundException(HttpStatus.NOT_FOUND.value(),
                        Collections.singletonList("Category ID not found !")));

        if (findCategory.getDeleted().equals(BigDecimal.ONE.intValue())){
            throw new NotfoundException(HttpStatus.NOT_FOUND.value(),
                    Collections.singletonList("Category is already deleted !"));
        }

        Product product = new Product();
        product.setCategory(findCategory);
        product.setName(request.getName());
        product.setQuantity(request.getQuantity());
        product.setPrice(request.getPrice());
        product.setDeleted(BigDecimal.ZERO.intValue());
        product.setCreatedAt(LocalDateTime.now());
        var saveProduct = productRepository.save(product);
        return buildResponse(saveProduct);
    }

    private ProductResponse buildResponse(Product product){
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .originalPrice(product.getPrice())
                .category(CategoryResponse.builder()
                        .id(product.getCategory().getId())
                        .name(product.getCategory().getName())
                        .build())
                .price(AppUtil.formatRupiah(product.getPrice()))
                .createdAt(AppUtil.formatDateTime(product.getCreatedAt()))
                .quantity(product.getQuantity())
                .build();
    }

    @Override
    public ProductResponse update(ProductRequest request) {
        var currentProduct = productRepository.findById(request.getId())
                .orElseThrow(() -> new NotfoundException(
                        HttpStatus.NOT_FOUND.value(),
                        Collections.singletonList("Product ID not found !")
                ));

        var currentCategory = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new NotfoundException(
                        HttpStatus.NOT_FOUND.value(),
                        Collections.singletonList("Category ID not found !")
                ));

        currentProduct.setName(request.getName());
        currentProduct.setCategory(currentCategory);
        currentProduct.setPrice(request.getPrice());
        currentProduct.setQuantity(request.getQuantity());
        var responseProduct = productRepository.save(currentProduct);
        return buildResponse(responseProduct);
    }

    @Override
    public ProductResponse findById(Long id) {
        return productRepository.findById(id)
                .map(this::buildResponse)
                .orElseThrow(() -> new NotfoundException(
                        HttpStatus.NOT_FOUND.value(),
                        Collections.singletonList("Product ID not found !")
                ));
    }

    @Override
    public ProductResponse deleteById(Long id) {
        return productRepository.findById(id)
                .map(product -> {
                    product.setDeleted(BigDecimal.ONE.intValue());
                    return productRepository.save(product);
                }).map(this::buildResponse)
                .orElseThrow(() -> new NotfoundException(
                        HttpStatus.NOT_FOUND.value(),
                        Collections.singletonList("Product ID not found !")
                ));
    }

    @Override
    public Page<ProductResponse> findAll(SearchProductRequest request) {
        log.info("process search product : {} ",request);

        Specification<Product> productSpecification = Specification.where(
                ProductSpecification.inquiryProductActive(BigDecimal.ZERO.intValue()));

        if (!request.getName().isEmpty()){
            productSpecification = productSpecification.and(ProductSpecification.inquiryByName(request.getName()));
        }

        if (!request.getCategoryId().isEmpty()){
            productSpecification = productSpecification.and(ProductSpecification.inquiryByCategory(Long.parseLong(request.getCategoryId())));
        }

        if (!request.getFromDate().isEmpty() || !request.getToDate().isEmpty()){
            LocalDate from = LocalDate.parse(request.getFromDate());
            LocalDate to = LocalDate.parse(request.getToDate());
            productSpecification = productSpecification.and(ProductSpecification
                    .inquiryByFromDateBetweenToDate(from,to));
        }

        return productRepository.findAll(productSpecification, PageRequest.of(request.getPage(),request.getSize()))
                .map(this::buildResponse);
    }

    @Override
    public byte[] generateReportPdf() {
        List<ProductReport> productReports = generateReportProduct();
        if (productReports.isEmpty()){
            throw new NotfoundException(HttpStatus.NOT_FOUND.value(),
                    Collections.singletonList("Products report is empty !"));
        }
        try{
            InputStream inputStream = getClass().getResourceAsStream("/reports/product_report.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(productReports);
            Map<String,Object> parameters = new HashMap<>();
            parameters.put("generatedBy", "Admin"); // by active token
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            return JasperExportManager.exportReportToPdf(jasperPrint);
        }catch (Exception e){
            log.error("failed generate report pdf product : {}",e.getMessage());
            throw new ConflictException(HttpStatus.CONFLICT.value(),
                    Collections.singletonList("Product report failed to generate !"));
        }
    }

    @Override
    public byte[] generateReportExcel() {
        List<ProductReport> productReports = generateReportProduct();
        if (productReports.isEmpty()){
            throw new NotfoundException(HttpStatus.NOT_FOUND.value(),
                    Collections.singletonList("Products report is empty !"));
        }
        try{
            InputStream inputStream = getClass().getResourceAsStream("/reports/product_report.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(productReports);
            Map<String,Object> parameters = new HashMap<>();
            parameters.put("generatedBy", "Admin"); //by active token
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            JRXlsExporter xlsExporter = new JRXlsExporter();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            xlsExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            xlsExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));

            SimpleXlsReportConfiguration configuration = new SimpleXlsReportConfiguration();
            configuration.setOnePagePerSheet(false);
            configuration.setDetectCellType(true);
            configuration.setCollapseRowSpan(false);
            xlsExporter.setConfiguration(configuration);
            xlsExporter.exportReport();
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("failed generate report xlsx product : {}",e.getMessage());
            throw new ConflictException(HttpStatus.CONFLICT.value(),
                    Collections.singletonList("Product report failed to generate !"));
        }
    }

    private List<ProductReport> generateReportProduct(){
        return productRepository.findAll().stream().map(product -> ProductReport
                        .builder()
                        .name(product.getName())
                        .formattedPrice(AppUtil.formatRupiah(product.getPrice()))
                        .categoryName(product.getCategory().getName())
                        .quantity(product.getQuantity())
                        .build())
                .toList();
    }

}
