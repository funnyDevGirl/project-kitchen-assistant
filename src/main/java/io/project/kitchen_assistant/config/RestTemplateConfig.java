package io.project.kitchen_assistant.config;

import io.project.kitchen_assistant.interceptors.GptHeaderInterceptor;
import io.project.kitchen_assistant.interceptors.TodoistHeaderInterceptor;
import lombok.AllArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@AllArgsConstructor
public class RestTemplateConfig {


    @Bean
    public RestTemplate restTemplateForGpt(RestTemplateBuilder builder,
                                           GptHeaderInterceptor gptHeaderInterceptor) {
        return builder
                .additionalInterceptors(gptHeaderInterceptor)
                .build();
    }

    @Bean
    public RestTemplate restTemplateForTodoist(RestTemplateBuilder builder,
                                               TodoistHeaderInterceptor todoistHeaderInterceptor) {
        return builder
                .additionalInterceptors(todoistHeaderInterceptor)
                .build();
    }
}
