package io.project.kitchen_assistant;

import io.project.kitchen_assistant.component.TokenScheduler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
@AllArgsConstructor
public class Application {

    private final TokenScheduler tokenScheduler;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    ApplicationRunner init() {
        return args -> initializeData();
    }

    private void initializeData() {
        tokenScheduler.fetchNewAccessToken();
    }
}
