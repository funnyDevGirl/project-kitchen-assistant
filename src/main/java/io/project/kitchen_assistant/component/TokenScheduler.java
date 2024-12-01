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

/**
 * {@code TokenScheduler} - класс, предназначенный для периодического обновления
 * токена доступа с помощью внешнего API.
 *
 * <p>
 * Этот компонент использует механизм планирования задач Spring для
 * автоматического получения нового токена доступа из API, обеспечивая актуальность
 * токена. Он запускает процесс получения токена через фиксированные интервалы,
 * позволяя другим компонентам приложения использовать действующий токен.
 * </p>
 */
@Slf4j
@EnableScheduling
@Component
public class TokenScheduler {

    private final AppConfig appConfig;
    private final RestTemplate restTemplateGptTokenApi;

    /**
     * Конструктор класса {@code TokenScheduler}.
     *
     * @param appConfig конфигурация приложения, содержащая информацию о токенах и API.
     * @param restTemplateGptTokenApi {@link RestTemplate}, используемый для обмена данными с API для получения токена.
     */
    public TokenScheduler(AppConfig appConfig, @Qualifier("restTemplateForGpt") RestTemplate restTemplateGptTokenApi) {
        this.appConfig = appConfig;
        this.restTemplateGptTokenApi = restTemplateGptTokenApi;
    }

    /**
     * Запланированный метод для периодического получения нового токена доступа.
     *
     * <p>
     * Этот метод вызывается с заданным фиксированным интервалом,
     * определяемым {@link ApplicationConstants#TOKEN_REFRESH_RATE_3_HOURS}.
     * Если новый токен успешно получен, он обновляет конфигурацию приложения и
     * записывает в лог сообщение об успешном обновлении. В противном случае
     * записывается сообщение об ошибке.
     * </p>
     */
    @Scheduled(fixedRate = ApplicationConstants.TOKEN_REFRESH_RATE_3_HOURS)
    public void scheduleFetchNewAccessToken() {
        String token = fetchNewAccessToken();

        if (token != null) {
            appConfig.setIAmToken(token);
            log.debug("Token updated!");
        } else {
            log.error("Failed to update token.");
        }
    }

    /**
     * Получает новый токен доступа из YandexGPT API.
     *
     * <p>
     * Этот метод выполняет HTTP-запрос к внешнему API для получения токена доступа.
     * Обрабатывает ошибки сетевого подключения и логирует информацию об успешности
     * операции. Возвращает токен в формате "Bearer \<token\>", либо {@code null},
     * если получение токена не удалось.
     * </p>
     *
     * @return токен доступа в виде строки, или {@code null} в случае ошибки.
     */
    public String fetchNewAccessToken() {
        String token = null;

        try {
            log.info("The receipt of the access token from Gpt begins");

            ResponseEntity<TokenResponse> response = restTemplateGptTokenApi.exchange(
                    appConfig.getGptTokenUrl(),
                    HttpMethod.POST,
                    new HttpEntity<>(appConfig.getJwtToken()),
                    TokenResponse.class);

            if (response != null && response.getBody() != null) {
                token = response.getBody().getIamToken();

            } else {
                log.warn("An empty or incorrect response was received.");
                return null;
            }

        } catch (HttpClientErrorException e) {
            log.error("Error when getting the access token from Gpt", e);
            return null;

        } catch (Exception e) {
            log.error("Unexpected error when receiving an access token", e);
            return null;
        }

        log.debug("Getting the access token successfully");

        if (token != null) {
            log.info("The access token was successfully received");

            return format("Bearer %s", token);

        } else {
            log.warn("The access token is null. A valid token cannot be returned.");
            return null;
        }
    }
}
