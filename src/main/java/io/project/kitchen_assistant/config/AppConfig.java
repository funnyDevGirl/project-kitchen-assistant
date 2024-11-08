package io.project.kitchen_assistant.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class AppConfig {

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

    @Value("${request-id}")
    private String todoistRequestId;

    @Value("${todoist-api-token}")
    private String todoistApiToken;

    @Value("${todoist-labels-api-url}")
    private String todoistLabelsApiUrl;

    @Value("${todoist-tasks-api-url}")
    private String todoistTasksApiUrl;

}
