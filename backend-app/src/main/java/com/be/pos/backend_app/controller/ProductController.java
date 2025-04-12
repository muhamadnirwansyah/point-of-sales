package com.be.pos.backend_app.controller;

import com.be.pos.backend_app.model.ApiResponse;
import com.be.pos.backend_app.model.ProductRequest;
import com.be.pos.backend_app.model.SearchProductRequest;
import com.be.pos.backend_app.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/product")
@RequiredArgsConstructor
@Tag(name = "Product Controller", description = "This is api for management related product.")
public class ProductController {

    private final ProductService productService;

    @PostMapping(value = "/save")
    @Operation(summary = "Save new product", description = "For simulation you should have Bearer <Token>")
    public ResponseEntity<ApiResponse> save(@RequestBody @Valid ProductRequest request){
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.isOk(productService.save(request)));
    }

    @PutMapping(value = "/update")
    @Operation(summary = "Update product existing", description = "For simulation you should have Bearer <Token>")
    public ResponseEntity<ApiResponse> update(@RequestBody @Valid ProductRequest request){
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.isOk(productService.update(request)));
    }

    @DeleteMapping(value = "/delete/{id}")
    @Operation(summary = "Delete product existing", description = "For simulation you should have Bearer <Token>")
    public ResponseEntity<ApiResponse> delete(@PathVariable("id")Long id){
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.isOk(productService.deleteById(id)));
    }

    @GetMapping(value = "/findById/{id}")
    @Operation(summary = "Find By ID product existing", description = "For simulation you should have Bearer <Token>")
    public ResponseEntity<ApiResponse> findById(@PathVariable("id")Long id){
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.isOk(productService.findById(id)));
    }

    @GetMapping(value = "/search")
    @Operation(summary = "Search product existing", description = "For simulation you should have Bearer <Token>")
    public ResponseEntity<ApiResponse> search(SearchProductRequest request){
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.isOk(productService.findAll(request)));
    }

    @GetMapping(value = "/report-pdf")
    @Operation(summary = "Reports pdf product existing", description = "For simulation you should have Bearer <Token>")
    public ResponseEntity<byte[]> generatePdf(){
        byte[] pdfBytes = productService.generateReportPdf();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=product_report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping(value = "/report-xlsx")
    @Operation(summary = "Reports xlsx product existing", description = "For simulation you should have Bearer <Token>")
    public ResponseEntity<byte[]> generateXlsx(){
        byte[] excelBytes = productService.generateReportExcel();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=product_report.xls")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelBytes);
    }
}
