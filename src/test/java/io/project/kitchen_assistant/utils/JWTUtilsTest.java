package io.project.kitchen_assistant.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class JWTUtilsTest {

    private JWTUtils jwtUtils;
    private JwtEncoder mockEncoder;
    private JwtDecoder mockDecoder;

    @BeforeEach
    public void setUp() {
        mockEncoder = Mockito.mock(JwtEncoder.class);
        mockDecoder = Mockito.mock(JwtDecoder.class);
        jwtUtils = new JWTUtils(mockEncoder, mockDecoder);
    }

    @Test
    public void testGenerateToken() {
        // Arrange
        String username = "test@example.com";
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(30, ChronoUnit.DAYS))
                .subject(username)
                .build();

        String expectedToken = "mocked.token.value";

        // Mock
        Map<String, Object> headers = new HashMap<>();
        headers.put("alg", "HS256");
        headers.put("typ", "JWT");

        Jwt mockJwt = new Jwt(
                expectedToken,
                now,
                now.plus(30, ChronoUnit.DAYS),
                headers,
                claims.getClaims());

        when(mockEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(mockJwt);

        // Act
        String token = jwtUtils.generateToken(username);

        // Assert
        assertEquals(expectedToken, token);
    }

    @Test
    public void testExtractUsername() {
        // Arrange
        String token = "mock.token.value";
        Jwt mockJwt = Mockito.mock(Jwt.class);
        when(mockJwt.getSubject()).thenReturn("test@example.com");
        when(mockDecoder.decode(token)).thenReturn(mockJwt);

        // Act
        String username = jwtUtils.extractUsername(token);

        // Assert
        assertEquals("test@example.com", username);
    }

    @Test
    public void testBuildAuthToken() {
        // Arrange
        String username = "test@example.com";

        // Act
        UsernamePasswordAuthenticationToken authToken = jwtUtils.buildAuthToken(username);

        // Assert
        assertEquals(username, authToken.getPrincipal());
        assertNull(authToken.getCredentials());
        assertEquals(0, authToken.getAuthorities().size());
    }
}

