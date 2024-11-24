package io.project.kitchen_assistant.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class AppConfig {

    @Value("${test-email}")
    private String testEmail;

    @Value("${test-first-name}")
    private String testFirstName;

    @Value("${test-last-name}")
    private String testLastName;

    @Value("${test-pass}")
    private String testPass;

    @Value("${gpt-token-url}")
    private String gptTokenUrl;

    @Value("${JWT_TOKEN}")
    private String jwtToken;

    @Value("${gpt-api-url}")
    private String gptApiUrl;

    @Value("${FOLDER_ID}")
    private String folderId;

    @Setter
    private String iAmToken;

    @Value("${todoist-tasks-api-url}")
    private String todoistTasksApiUrl;

    @Value("${spring.security.oauth2.client.registration.todoist.client-id}")
    private String todoistClientId;

    @Value("${spring.security.oauth2.client.registration.todoist.client-secret}")
    private String todoistClientSecret;

    @Value("${spring.security.oauth2.client.registration.todoist.scope}")
    private String todoistScope;

    @Value("${spring.security.oauth2.client.provider.todoist.authorization-uri}")
    private String todoistAuthorizationUri;

    @Value("${spring.security.oauth2.client.registration.todoist.redirect-uri}")
    private String todoistAuthRedirectUri;

    @Value("${spring.security.oauth2.client.provider.todoist.token-uri}")
    private String exchangeTodoistTokenUri;
}
