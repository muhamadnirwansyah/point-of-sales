package com.be.pos.backend_app.controller;

import com.be.pos.backend_app.model.ApiResponse;
import com.be.pos.backend_app.model.AuthenticationRequest;
import com.be.pos.backend_app.model.RegisterRequest;
import com.be.pos.backend_app.service.AuthenticationServcie;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication Controller", description = "This is API for manage register/sign-in management account")
public class AuthenticationController {

    private final AuthenticationServcie authenticationServcie;

    @PostMapping(value = "/register")
    @Operation(summary = "This is method/API for register new user",description = "Register api you should have valid email and phone number")
    public ResponseEntity<ApiResponse> register(@RequestBody @Valid RegisterRequest request){
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.isOk(authenticationServcie.register(request)));
    }

    @PostMapping(value = "/sign-in")
    @Operation(summary = "This is method/API for login system", description = "You should have account valid to access this system.")
    public ResponseEntity<ApiResponse> signIn(@RequestBody @Valid AuthenticationRequest request){
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.isOk(authenticationServcie.signIn(request)));
    }

}
