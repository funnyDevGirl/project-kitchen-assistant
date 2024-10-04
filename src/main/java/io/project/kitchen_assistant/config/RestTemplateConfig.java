package io.project.kitchen_assistant.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;


// TODO: fix headers
@Configuration
public class RestTemplateConfig {

//    @Bean
//    public RestTemplate restTemplate(@Value("${FOLDER_ID}") String id,
//                                     @Value("${IAM_TOKEN}") String token) {
//        return new RestTemplateBuilder()
//                .requestCustomizers(clientHttpRequest -> {
//                    clientHttpRequest.getHeaders().add("x-folder-id", id);
//                    clientHttpRequest.getHeaders().add("Authorization", token);
//                })
//                .build();
//    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
