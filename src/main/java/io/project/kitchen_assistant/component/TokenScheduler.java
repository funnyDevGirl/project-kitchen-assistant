package io.project.kitchen_assistant.component;

import io.project.kitchen_assistant.config.AppConfig;
import io.project.kitchen_assistant.dto.recipes.gpt.TokenResponse;
import io.project.kitchen_assistant.config.ApplicationConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static java.lang.String.format;

@Slf4j
@EnableScheduling
@Component
public class TokenScheduler {

    private final AppConfig appConfig;
    private final RestTemplate restTemplateGptTokenApi;

    public TokenScheduler(AppConfig appConfig, @Qualifier("restTemplateForGptToken") RestTemplate restTemplateGptTokenApi) {
        this.appConfig = appConfig;
        this.restTemplateGptTokenApi = restTemplateGptTokenApi;
    }

    @Scheduled(fixedRate = ApplicationConstants.TOKEN_REFRESH_RATE)
    public void scheduleFetchNewAccessToken() {
        String token = fetchNewAccessToken();

        if (token != null) {
            appConfig.setIAmToken(token);
            log.info("Token updated");
        } else {
            log.error("Failed to update token.");
        }
    }

    public String fetchNewAccessToken() {
        String token = null;

        try {
            log.info("The receipt of the access token from Gpt begins");

            ResponseEntity<TokenResponse> response = restTemplateGptTokenApi.exchange(appConfig.getGptTokenUrl(),
                    HttpMethod.POST, new HttpEntity<>(appConfig.getJwtToken()), TokenResponse.class);

            if (response.getBody() != null) {
                token = response.getBody().getIamToken();
            } else {
                return null;
            }

        } catch (HttpClientErrorException e) {
            log.error("Error when getting the access token from Gpt", e);
        }
        log.info("Getting the access token successfully");

        return format("Bearer %s", token);
    }
}
