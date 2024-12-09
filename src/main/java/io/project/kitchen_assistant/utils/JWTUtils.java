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

/**
 * Утилитный класс для работы с JSON Web Token (JWT).
 * <p>
 * Этот компонент используется для генерации, извлечения и построения аутентификационных токенов
 * на основе JWT. Он предоставляет методы для создания токенов с определенными
 * заявками (claims) и извлечения информации из токенов.
 * </p>
 */
@Component
@AllArgsConstructor
public class JWTUtils {

    private final JwtEncoder encoder;
    private final JwtDecoder decoder;

    /**
     * Генерирует новый JWT для указанного пользователя.
     *
     * @param username имя пользователя, для которого будет сгенерирован токен
     * @return сгенерированный JWT в виде строки
     */
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

    /**
     * Извлекает имя пользователя из заданного JWT.
     *
     * @param token JWT, из которого необходимо извлечь имя пользователя
     * @return имя пользователя, указанное в токене
     * @throws IllegalArgumentException если токен не является действительным
     */
    public String extractUsername(String token) {
        Jwt jwt = this.decoder.decode(token);
        return jwt.getSubject();
    }

    /**
     * Строит объект аутентификационного токена на основе заданного имени пользователя.
     *
     * @param username email пользователя, для которого создается токен.
     * @return экземпляр {@link UsernamePasswordAuthenticationToken} с указанным именем пользователя
     */
    public UsernamePasswordAuthenticationToken buildAuthToken(final String username) {
        return new UsernamePasswordAuthenticationToken(
                username,
                null,
                List.of()
        );
    }
}
