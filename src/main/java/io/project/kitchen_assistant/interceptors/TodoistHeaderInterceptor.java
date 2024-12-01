package io.project.kitchen_assistant.interceptors;

import io.project.kitchen_assistant.component.DataStorage;
import io.project.kitchen_assistant.config.AppConfig;
import io.project.kitchen_assistant.exception.UserNotFoundException;
import io.project.kitchen_assistant.model.User;
import io.project.kitchen_assistant.repository.UserRepository;
import io.project.kitchen_assistant.service.UserService;
import io.project.kitchen_assistant.utils.UserUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;
import static java.lang.String.format;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

/**
 * {@code TodoistHeaderInterceptor} - перехватчик HTTP-запросов
 * для обработки запросов к API Todoist.
 *
 * <p>
 * Этот компонент добавляет необходимые заголовки к HTTP-запросам
 * в зависимости от типа запроса иURI, а также управляет
 * специфическими задачами, такими как получение пользовательских токенов.
 * Он реализует интерфейс {@link ClientHttpRequestInterceptor}
 * для настройки запросов, отправляемых к API Todoist.
 * </p>
 */
@Slf4j
@AllArgsConstructor
@Component
public class TodoistHeaderInterceptor implements ClientHttpRequestInterceptor {

    private final UserRepository userRepository;
    private final DataStorage authorizationCodeStorage;
    private final UserUtils userUtils;
    private final AppConfig appConfig;

    /**
     * Перехватывает HTTP-запросы перед их выполнением.
     *
     * <p>
     * Этот метод добавляет заголовок контента, устанавливает токен
     * авторизации на основе текущего пользователя и обрабатывает
     * запросы на специфические URL-адреса, такие как API задач Todoist
     * и обмен токенами. Если токен недоступен, выбрасывается
     * {@link IllegalArgumentException}.
     * </p>
     *
     * @param request   объект {@link HttpRequest}, представляющий
     *                  исходящий HTTP-запрос.
     * @param body      массив байтов, представляющий тело запроса.
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

        log.info("Sending request to URI: {}", request.getURI());

        String currentUserEmail = userUtils.getCurrentUserEmail();
        log.debug("Received email: '{}'", currentUserEmail);

        if (request.getURI().toString().contains(appConfig.getTodoistTasksApiUrl())) {

            User user = userRepository.findByEmail(currentUserEmail).orElseThrow(
                    () -> new UserNotFoundException(format("User with email: '%s' not found", currentUserEmail)));

            String userToken = user.getTodoistToken();

            setRequestHeaders(request, userToken);

        } else if (request.getURI().toString().equals(appConfig.getExchangeTodoistTokenUri())) {

            request.getHeaders().setContentType(APPLICATION_FORM_URLENCODED);

            String code = authorizationCodeStorage.get(currentUserEmail);

            if (code != null) {

                String requestBody = format(
                        "client_id=%s&client_secret=%s&code=%s&redirect_uri=%s",
                        appConfig.getTodoistClientId(),
                        appConfig.getTodoistClientSecret(),
                        code,
                        appConfig.getTodoistAuthRedirectUri()
                );

                body = requestBody.getBytes(StandardCharsets.UTF_8);

                request.getHeaders().setContentLength(body.length);

            } else {
                log.error("Authorization code is missing for user: {}", currentUserEmail);
                throw new IllegalArgumentException("Authorization code is missing in session");
            }
        }

        return execution.execute(request, body);
    }

    /**
     * Устанавливает заголовки запроса для HTTP-запросов к Todoist API.
     *
     * <p>
     * Этот метод добавляет различные заголовки в зависимости от метода HTTP
     * запроса (POST, DELETE или GET) и устанавливает токен авторизации.
     * Заголовок {@code X-Request-Id} генерируется для запросов POST.
     * </p>
     *
     * @param request   объект {@link HttpRequest}, к которому добавляются заголовки.
     * @param userToken токен авторизации для текущего пользователя.
     */
    private void setRequestHeaders(HttpRequest request, String userToken) {

        if (request.getMethod().equals(HttpMethod.POST)) {
            request.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            request.getHeaders().set("X-Request-Id", UUID.randomUUID().toString());
            request.getHeaders().set("Authorization", userToken);

            log.debug("Sending POST request to URI: {}", request.getURI());

        } else if (request.getMethod().equals(HttpMethod.DELETE) || request.getMethod().equals(HttpMethod.GET)) {
            request.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            request.getHeaders().set("Authorization", userToken);

            log.debug("Sending {} request to URI: {}", request.getMethod().name(), request.getURI());
        }

        log.debug("Added {} request headers", request.getHeaders().size());
    }
}
