package com.be.pos.backend_app.service;

import com.be.pos.backend_app.model.CategoryRequest;
import com.be.pos.backend_app.model.CategoryResponse;
import com.be.pos.backend_app.model.DropdownCategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {

    CategoryResponse save(CategoryRequest request);
    CategoryResponse update(CategoryRequest request);
    CategoryResponse findById(Long id);
    CategoryResponse deleteById(Long id);
    Page<CategoryResponse> findAll(Pageable pageable);
    List<DropdownCategoryResponse> dropdownCategories();
}
