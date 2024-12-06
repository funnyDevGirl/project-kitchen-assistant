package io.project.kitchen_assistant.utils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class JWTUtils {

    private final JwtEncoder encoder;
    private final JwtDecoder decoder;

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

    public String extractUsername(String token) {
        Jwt jwt = this.decoder.decode(token);
        return jwt.getSubject();
    }

    public UsernamePasswordAuthenticationToken buildAuthToken(final String username) {
        return new UsernamePasswordAuthenticationToken(
                username,
                null,
                List.of()
        );
    }
}
