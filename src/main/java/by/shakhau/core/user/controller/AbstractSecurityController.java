package by.shakhau.core.user.controller;

import by.shakhau.core.user.service.impl.JwtService;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
public abstract class AbstractSecurityController {

    private final JwtService jwtService;

    protected UUID findUserId(String authHeader) {
        String accessToken = authHeader.substring("Bearer ".length());
        Claims claims = jwtService.getClaims(accessToken);
        return UUID.fromString(claims.getSubject());
    }
}
