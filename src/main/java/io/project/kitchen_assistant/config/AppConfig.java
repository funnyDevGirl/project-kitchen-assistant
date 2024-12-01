package io.project.kitchen_assistant.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * {@code AppConfig} - класс конфигурации приложения для
 * хранения параметров, загружаемых из файла конфигурации.
 *
 * <p>
 * Этот класс используется для получения различных свойств
 * приложения, таких как учетные данные пользователя, URL-адреса
 * API и настройки OAuth2. Все значения считываются из
 * внешнего конфигурационного файла, позволяя удобно
 * управлять параметрами при развертывании приложения.
 * </p>
 */
@Configuration
@Getter
public class AppConfig {

    /**
     * Тестовый email пользователя.
     */
    @Value("${test-email}")
    private String testEmail;

    /**
     * Тестовое имя пользователя.
     */
    @Value("${test-first-name}")
    private String testFirstName;

    /**
     * Тестовая фамилия пользователя.
     */
    @Value("${test-last-name}")
    private String testLastName;

    /**
     * Тестовый пароль пользователя.
     */
    @Value("${test-pass}")
    private String testPass;

    /**
     * URL для получения токена YandexGPT.
     */
    @Value("${gpt-token-url}")
    private String gptTokenUrl;

    /**
     * JWT токен.
     */
    @Value("${JWT_TOKEN}")
    private String jwtToken;

    /**
     * URL для взаимодействия с API YandexGPT.
     */
    @Value("${gpt-api-url}")
    private String gptApiUrl;

    /**
     * Идентификатор папки для Todoist.
     */
    @Value("${FOLDER_ID}")
    private String folderId;

    /**
     * Токен доступа для текущего пользователя.
     */
    @Setter
    private String iAmToken;

    /**
     * URL для API задач Todoist.
     */
    @Value("${todoist-tasks-api-url}")
    private String todoistTasksApiUrl;

    /**
     * Идентификатор клиента для Todoist OAuth2.
     */
    @Value("${spring.security.oauth2.client.registration.todoist.client-id}")
    private String todoistClientId;

    /**
     * Секрет клиента для Todoist OAuth2.
     */
    @Value("${spring.security.oauth2.client.registration.todoist.client-secret}")
    private String todoistClientSecret;

    /**
     * Область доступа для Todoist OAuth2.
     */
    @Value("${spring.security.oauth2.client.registration.todoist.scope}")
    private String todoistScope;

    /**
     * URL-адрес авторизации Todoist.
     */
    @Value("${spring.security.oauth2.client.provider.todoist.authorization-uri}")
    private String todoistAuthorizationUri;

    /**
     * URL обратного вызова для аутентификации Todoist.
     */
    @Value("${spring.security.oauth2.client.registration.todoist.redirect-uri}")
    private String todoistAuthRedirectUri;

    /**
     * URL для обмена токенами Todoist.
     */
    @Value("${spring.security.oauth2.client.provider.todoist.token-uri}")
    private String exchangeTodoistTokenUri;
}
