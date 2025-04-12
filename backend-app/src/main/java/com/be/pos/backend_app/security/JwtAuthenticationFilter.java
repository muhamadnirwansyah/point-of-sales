package com.be.pos.backend_app.security;

import com.be.pos.backend_app.entity.Account;
import com.be.pos.backend_app.repository.AccountRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AccountRepository accountRepository;
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {


        final String authHeader = request.getHeader("Authorization");
        final String jwtToken;
        final String email;

        if (Objects.isNull(authHeader) || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return;
        }

        jwtToken = authHeader.substring(7);
        email = jwtService.extractAccount(jwtToken).getEmail();
        if (!Objects.isNull(email) && Objects.isNull(SecurityContextHolder.getContext().getAuthentication())){
            Optional<Account> accountOptional = accountRepository.getAccountByEmail(email);
            if (!accountOptional.isPresent()){
                filterChain.doFilter(request,response);
                return;
            }
            if (jwtService.isTokenValid(jwtToken, accountOptional.get())){
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        accountOptional.get(),
                        null,
                        accountOptional.get().getAuthorities());

                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        filterChain.doFilter(request,response);
    }
}
