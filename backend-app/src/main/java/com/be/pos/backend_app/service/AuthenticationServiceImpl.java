package com.be.pos.backend_app.service;

import com.be.pos.backend_app.entity.Account;
import com.be.pos.backend_app.entity.Roles;
import com.be.pos.backend_app.exception.ConflictException;
import com.be.pos.backend_app.exception.NotfoundException;
import com.be.pos.backend_app.exception.UnauthorizedException;
import com.be.pos.backend_app.model.*;
import com.be.pos.backend_app.repository.AccountRepository;
import com.be.pos.backend_app.repository.RolesRepository;
import com.be.pos.backend_app.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationServcie{

    private final AccountRepository accountRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final RolesRepository rolesRepository;
    private final AuthenticationManager authenticationManager;

    @Override
    public RegisterResponse register(RegisterRequest request) {
        log.info("PROCESS REGISTER --- {} ",request);
        long checkEmail = accountRepository.checkEmail(request.getEmail());
        long checkPhoneNumber = accountRepository.checkPhoneNumber(request.getPhoneNumber());

        List<String> errors = new ArrayList<>();
        if (checkEmail > 0){
            errors.add("Email is already in use !");
        }

        if (checkPhoneNumber > 0){
            errors.add("Phone number is already in use !");
        }

        if (!errors.isEmpty()){
            throw new ConflictException(HttpStatus.CONFLICT.value(), errors);
        }

        Account account = new Account();
        account.setName(request.getName());
        account.setEmail(request.getEmail());
        account.setDeleted(BigDecimal.ZERO.intValue());
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        account.setPhoneNumber(request.getPhoneNumber());
        account.setRoles(checkRoles(request.getRolesId()));
        var response = accountRepository.save(account);
        return buildResponse(response);
    }

    private RegisterResponse buildResponse(Account account){
        return RegisterResponse.builder()
                .id(account.getId())
                .email(account.getEmail())
                .build();
    }

    private Roles checkRoles(Long id){
        return rolesRepository.findById(id)
                .orElseThrow(() -> new NotfoundException(HttpStatus.NOT_FOUND.value(),
                        Collections.singletonList("Roles not found !")));
    }

    @Override
    public AuthenticationResponse signIn(AuthenticationRequest request) {
        log.info("PROCESS SIGNIN --- {} ",request.getEmail());
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
        ));
        var checkDataAccount = accountRepository.getAccountByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException(HttpStatus.UNAUTHORIZED.value(),
                        Collections.singletonList("Invalid credentials username or password !")));

        var jwtToken = jwtService.generateToken(checkDataAccount);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .roles(checkDataAccount.getRoles().getName())
                .build();
    }

    @Override
    public ExtractJwtTokenResponse extractJwtToken(ExtractJwtTokenRequest request) {
        var originalToken = request.getToken().substring(7);
        return jwtService.extractAccount(originalToken);
    }
}
