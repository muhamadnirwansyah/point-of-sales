package com.be.pos.backend_app.service;

import com.be.pos.backend_app.entity.Category;
import com.be.pos.backend_app.exception.NotfoundException;
import com.be.pos.backend_app.model.CategoryRequest;
import com.be.pos.backend_app.model.CategoryResponse;
import com.be.pos.backend_app.model.DropdownCategoryResponse;
import com.be.pos.backend_app.repository.CategoryRepository;
import com.be.pos.backend_app.specification.CategorySpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService{

    private final CategoryRepository categoryRepository;
    final String messageCategoryNotfound = "Category ID not found !";

    @Override
    public CategoryResponse save(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        category.setDeleted(BigDecimal.ZERO.intValue());
        category.setDescription(request.getDescription());
        var response = categoryRepository.save(category);
        return buildResponse(response);
    }

    private CategoryResponse buildResponse(Category category){
        return new CategoryResponse(category.getId(),category.getDescription(),category.getName(),category.getDeleted());
    }

    @Override
    public CategoryResponse update(CategoryRequest request) {
        return categoryRepository.findById(request.getId())
                .map(category -> {
                    category.setName(request.getName());
                    category.setDescription(request.getDescription());
                    return categoryRepository.save(category);
                })
                .map(this::buildResponse)
                .orElseThrow(() -> new NotfoundException(HttpStatus.NOT_FOUND.value(),
                        Collections.singletonList(messageCategoryNotfound)));
    }

    @Override
    public CategoryResponse findById(Long id) {
        return categoryRepository.findById(id)
                .map(this::buildResponse)
                .orElseThrow(() -> new NotfoundException(
                        HttpStatus.NOT_FOUND.value(),
                        Collections.singletonList(messageCategoryNotfound)));
    }

    @Override
    public CategoryResponse deleteById(Long id) {
        return categoryRepository.findById(id)
                .map(category -> {
                    category.setDeleted(BigDecimal.ONE.intValue());
                    return categoryRepository.save(category);
                }).map(this::buildResponse)
                .orElseThrow(() -> new NotfoundException(HttpStatus.NOT_FOUND.value(),
                        Collections.singletonList(messageCategoryNotfound)));
    }

    @Override
    public Page<CategoryResponse> findAll(Pageable pageable) {
        Specification<Category> categorySpec = CategorySpecification.inquiryByActiveItem(0);
        Page<Category> categoryPage = categoryRepository.findAll(categorySpec,
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()));
        return categoryPage.map(this::buildResponse);
    }

    @Override
    public List<DropdownCategoryResponse> dropdownCategories() {
        return categoryRepository.listCategories().stream()
                .map(category -> DropdownCategoryResponse.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .build())
                .toList();
    }
}
