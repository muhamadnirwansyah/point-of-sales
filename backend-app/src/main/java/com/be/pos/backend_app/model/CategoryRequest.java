package com.be.pos.backend_app.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryRequest {

    private Long id;
    @NotBlank(message = "Name is required !")
    private String name;
    @NotBlank(message = "Description is required !")
    private String description;
}
