package io.project.kitchen_assistant.config;

import io.project.kitchen_assistant.interceptors.GptHeaderInterceptor;
import io.project.kitchen_assistant.interceptors.GptTokenHeaderInterceptor;
import io.project.kitchen_assistant.interceptors.TodoistHeaderForGetAndDeleteInterceptor;
import io.project.kitchen_assistant.interceptors.TodoistHeaderForPostInterceptor;
import lombok.AllArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@AllArgsConstructor
public class RestTemplateConfig {

    private final AppConfig appConfig;

    @Bean
    public RestTemplate restTemplateForGpt(RestTemplateBuilder builder,
                                           GptHeaderInterceptor gptHeaderInterceptor) {
        return builder
                .additionalInterceptors(gptHeaderInterceptor)
                .build();
    }

    @Bean
    public RestTemplate restTemplateForGptToken(RestTemplateBuilder builder,
                                                GptTokenHeaderInterceptor gptTokenHeaderInterceptor) {
        return builder
                .additionalInterceptors(gptTokenHeaderInterceptor)
                .build();
    }

    @Bean
    public RestTemplate restTemplateForGetAndDeleteOnTodoist(RestTemplateBuilder builder,
                                                             TodoistHeaderForGetAndDeleteInterceptor todoistHeaderForGetAndDeleteInterceptor) {
        return builder
                .additionalInterceptors(todoistHeaderForGetAndDeleteInterceptor)
                .build();
    }

    @Bean
    public RestTemplate restTemplateForPostToTodoist(RestTemplateBuilder builder,
                                                     TodoistHeaderForPostInterceptor todoistHeaderForPostInterceptor) {
        return builder
                .additionalInterceptors(todoistHeaderForPostInterceptor)
                .build();
    }
}
