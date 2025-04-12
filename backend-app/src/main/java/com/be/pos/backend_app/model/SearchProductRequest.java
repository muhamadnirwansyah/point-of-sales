package com.be.pos.backend_app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchProductRequest {

    private String name = "";
    private String categoryId = "";
    private String fromDate = "";
    private String toDate = "";
    private Integer page = 0;
    private Integer size = 10;

}
