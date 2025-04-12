package com.be.pos.backend_app.security;

import com.be.pos.backend_app.entity.Account;
import com.be.pos.backend_app.model.ExtractJwtTokenResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.secret.key}")
    private String jwtSecretKey;
    @Value("${jwt.expiration.time}")
    private Long jwtExpiration;

    private final ObjectMapper objectMapper;

    public <T> T extractAllClaimsFromToken(String token, Function<Claims,T> claimsResolver){
        final Claims claims = extractAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private String buildSubject(ExtractJwtTokenResponse extractJwtTokenResponse){
        try{
            return objectMapper.writeValueAsString(extractJwtTokenResponse);
        }catch (JsonProcessingException e){
            log.error("Failed convert to json string !");
            throw new RuntimeException(e);
        }
    }

    private Key getSignKey(){
        byte[] keybytes = Base64.getDecoder().decode(jwtSecretKey);
        return Keys.hmacShaKeyFor(keybytes);
    }

    public ExtractJwtTokenResponse extractAccount(String token){
        try{
            return objectMapper.readValue(extractAllClaimsFromToken(token,Claims::getSubject), ExtractJwtTokenResponse.class);
        }catch (JsonProcessingException e){
            log.error("Failed extract account from jwt token ! : {}",e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public String generateToken(Map<String,Object> extractClaims, Account account){
        var buildCurrentSubject = ExtractJwtTokenResponse.builder()
                .id(account.getId())
                .email(account.getEmail())
                .phoneNumber(account.getPhoneNumber())
                .roles(account.getRoles().getName())
                .build();
        return Jwts.builder()
                .setClaims(extractClaims)
                .setSubject(buildSubject(buildCurrentSubject))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateToken(Account account){
        return generateToken(new HashMap<>(), account);
    }

    public boolean isTokenValid(String token, Account account){
        final String email = extractAccount(token).getEmail();
        return (email.equals(account.getEmail()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token){
        return extractExpirationTime(token).before(new Date());
    }

    public Date extractExpirationTime(String token){
        return extractAllClaimsFromToken(token, Claims::getExpiration);
    }

    private Claims extractAllClaimsFromToken(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
