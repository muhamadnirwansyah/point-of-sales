package com.be.pos.backend_app.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class UnauthorizedException extends RuntimeException{

    private int status;

    public UnauthorizedException(int status, List<String> messages){
        super(String.join(",",messages));
        this.status = status;
    }
}
