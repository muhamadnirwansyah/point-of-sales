package com.be.pos.backend_app.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {

    private Long id;
    @NotBlank(message = "Name is required !")
    private String name;
    @NotNull(message = "Price is required !")
    private BigDecimal price;
    @NotNull(message = "Quantity is required !")
    private Integer quantity;
    @NotNull(message = "Category is required !")
    private Long categoryId;

}
