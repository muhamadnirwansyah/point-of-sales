package com.be.pos.backend_app.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse {

    private Integer status;
    private String message;
    private List<String> errors;
    private Object data;

    public static ApiResponse isOk(Object data){
        return ApiResponse.builder()
                .data(data)
                .message(HttpStatus.OK.getReasonPhrase())
                .status(HttpStatus.OK.value())
                .errors(null)
                .build();
    }

    public static ApiResponse failed(List<String> errors, Integer status){
        return ApiResponse.builder()
                .data(null)
                .message("Failed")
                .status(status)
                .errors(errors)
                .build();
    }
}
