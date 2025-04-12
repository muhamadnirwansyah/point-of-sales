package com.be.pos.backend_app.controller;

import com.be.pos.backend_app.model.ApiResponse;
import com.be.pos.backend_app.model.CategoryRequest;
import com.be.pos.backend_app.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/category")
@RequiredArgsConstructor
@Tag(name = "Category Controller", description = "This is api for management related category.")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping(value = "/save")
    @Operation(summary = "Save new category", description = "For simulation you should have Bearer <Token>")
    public ResponseEntity<ApiResponse> save(@RequestBody @Valid CategoryRequest request){
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.isOk(categoryService.save(request)));
    }

    @PutMapping(value = "/update")
    @Operation(summary = "Update category existing", description = "For simulation you should have Bearer <Token>")
    public ResponseEntity<ApiResponse> update(@RequestBody @Valid CategoryRequest request){
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.isOk(categoryService.update(request)));
    }

    @DeleteMapping(value = "/delete/{id}")
    @Operation(summary = "Delete category existing", description = "For simulation you should have Bearer <Token>")
    public ResponseEntity<ApiResponse> delete(@PathVariable("id")Long id){
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.isOk(categoryService.deleteById(id)));
    }

    @GetMapping(value = "/findById/{id}")
    @Operation(summary = "Find By ID category existing", description = "For simulation you should have Bearer <Token>")
    public ResponseEntity<ApiResponse> findById(@PathVariable("id")Long id){
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.isOk(categoryService.findById(id)));
    }

    @GetMapping(value = "/search")
    @Operation(summary = "Search category existing", description = "For simulation you should have Bearer <Token>")
    public ResponseEntity<ApiResponse> search(Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.isOk(categoryService.findAll(pageable)));
    }

    @GetMapping(value = "/dropdown")
    public ResponseEntity<ApiResponse> dropdown(){
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.isOk(categoryService.dropdownCategories()));
    }
}
