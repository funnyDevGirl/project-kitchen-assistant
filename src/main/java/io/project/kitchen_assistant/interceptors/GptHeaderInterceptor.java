package io.project.kitchen_assistant.interceptors;

import io.project.kitchen_assistant.config.AppConfig;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import java.io.IOException;

/**
 * {@code GptHeaderInterceptor} - перехватчик HTTP-запросов,
 * предназначенный для добавления необходимых заголовков
 * к запросам, отправляемым к YandexGPT API.
 *
 * <p>
 * Этот компонент реализует интерфейс {@link ClientHttpRequestInterceptor}
 * и добавляет заголовки к запросам, в зависимости от URI запроса.
 * Заголовки включают идентификатор папки и токен авторизации,
 * используемый для доступа к GPT API.
 * </p>
 */
@Slf4j
@AllArgsConstructor
@Component
public class GptHeaderInterceptor implements ClientHttpRequestInterceptor {

    private final AppConfig appConfig;

    /**
     * Перехватывает HTTP-запросы перед их выполнением.
     *
     * <p>
     * Этот метод добавляет заголовок содержимого
     * как {@code application/json}, а также устанавливает дополнительные
     * заголовки в зависимости от URI запроса. Для запросов к
     * YandexGPT API добавляются токен авторизации и идентификатор
     * папки. Журнализируется информация о процессе обработки
     * запросов для отладки и мониторинга.
     * </p>
     *
     * @param request объект {@link HttpRequest}, представляющий
     *                исходящий HTTP-запрос.
     * @param body    массив байтов, представляющий тело запроса.
     * @param execution объект {@link ClientHttpRequestExecution},
     *                  используемый для выполнения запроса.
     * @return {@link ClientHttpResponse} объект, представляющий
     *         ответ на выполненный запрос.
     * @throws IOException если происходит ошибка ввода-вывода
     *                     во время выполнения запроса.
     */
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {


        request.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        log.info("Sending request to URI: {}", request.getURI());

        if (request.getURI().toString().equals(appConfig.getGptApiUrl())) {

            request.getHeaders().set("x-folder-id", appConfig.getFolderId());
            request.getHeaders().set("Authorization", appConfig.getIAmToken());

            log.info("Set headers for GPT API request.");

        } else if (request.getURI().toString().equals(appConfig.getGptTokenUrl())) {

            log.info("Request is going to the GPT token URL.");
        }

        return execution.execute(request, body);
    }
}
