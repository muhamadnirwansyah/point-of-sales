package com.be.pos.backend_app.controller;

import com.be.pos.backend_app.model.ApiResponse;
import com.be.pos.backend_app.model.ExtractJwtTokenRequest;
import com.be.pos.backend_app.service.AuthenticationServcie;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/account")
@RequiredArgsConstructor
@Tag(name = "Account Controller", description = "This is api for management related account.")
public class AccountController {

    private final AuthenticationServcie authenticationServcie;

    @PostMapping(value = "/token-active")
    @Operation(summary = "Check token valid or not.", description = "For simulation you should have Bearer <Token>")
    public ResponseEntity<ApiResponse> tokenActive(@RequestBody ExtractJwtTokenRequest request){
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.isOk(authenticationServcie.extractJwtToken(request)));
    }
}
