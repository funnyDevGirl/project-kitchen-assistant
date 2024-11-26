package io.project.kitchen_assistant.utils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.AllArgsConstructor;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class JWTUtils {

    private final JwtEncoder encoder;
    private final JwtDecoder decoder; // Добавьте декодер


    public String generateToken(String username) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(30, ChronoUnit.DAYS))
                .subject(username)
                .build();
        return this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
