package io.project.kitchen_assistant.component;

import io.project.kitchen_assistant.config.AppConfig;
import io.project.kitchen_assistant.dto.recipes.gpt.TokenResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import static java.lang.String.format;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;

public class TokenSchedulerTest {

    @Mock
    private AppConfig appConfig;

    @Mock
    private RestTemplate restTemplateGptTokenApi;

    @InjectMocks
    private TokenScheduler tokenScheduler;

    @Mock
    private TokenResponse tokenResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(appConfig.getGptTokenUrl()).thenReturn("http://example.com/token");
        when(appConfig.getJwtToken()).thenReturn("jwtToken");
    }

    @Test
    void testScheduleFetchNewAccessTokenWithSuccess() {
        String mockToken = "mockToken123";

        // Настраиваем мок ответа
        when(restTemplateGptTokenApi.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(TokenResponse.class)))
                .thenReturn(ResponseEntity.ok(tokenResponse));
        when(tokenResponse.getIamToken()).thenReturn(mockToken);

        // Act
        tokenScheduler.scheduleFetchNewAccessToken();

        // Assert
        verify(appConfig).setIAmToken(format("Bearer %s", mockToken));
    }

    @Test
    void testScheduleFetchNewAccessTokenWithEmptyResponse() {
        when(restTemplateGptTokenApi.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(TokenResponse.class)))
                .thenReturn(ResponseEntity.ok(null));

        // Act
        tokenScheduler.scheduleFetchNewAccessToken();

        // Assert
        verify(appConfig, never()).setIAmToken(anyString());
    }

    @Test
    void testScheduleFetchNewAccessTokenWhenTokenIsNull() {
        when(restTemplateGptTokenApi.exchange(anyString(),
                eq(HttpMethod.POST), any(HttpEntity.class), eq(TokenResponse.class)))
                .thenReturn(ResponseEntity.ok(tokenResponse));
        when(tokenResponse.getIamToken()).thenReturn(null);

        // Act
        tokenScheduler.scheduleFetchNewAccessToken();

        // Assert
        verify(appConfig, never()).setIAmToken(anyString());
    }

    @Test
    void testScheduleFetchNewAccessTokenWithHttpClientErrorException() {
        when(restTemplateGptTokenApi.exchange(anyString(),
                eq(HttpMethod.POST), any(HttpEntity.class), eq(TokenResponse.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        // Act
        tokenScheduler.scheduleFetchNewAccessToken();

        // Assert
        verify(appConfig, never()).setIAmToken(anyString());
    }

    @Test
    void testScheduleFetchNewAccessTokenWithUnexpectedException() {
        when(restTemplateGptTokenApi.exchange(anyString(),
                eq(HttpMethod.POST), any(HttpEntity.class), eq(TokenResponse.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Act
        tokenScheduler.scheduleFetchNewAccessToken();

        // Assert
        verify(appConfig, never()).setIAmToken(anyString());
    }
}
