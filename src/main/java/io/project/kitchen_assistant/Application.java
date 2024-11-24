package io.project.kitchen_assistant;

import io.project.kitchen_assistant.component.TokenScheduler;
import io.project.kitchen_assistant.config.AppConfig;
import io.project.kitchen_assistant.dto.users.UserCreateDTO;
import io.project.kitchen_assistant.model.User;
import io.project.kitchen_assistant.repository.UserRepository;
import io.project.kitchen_assistant.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import java.util.Optional;

@Slf4j
@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
@AllArgsConstructor
public class Application {

    private final UserService userService;
    private final UserRepository userRepository;
    private final TokenScheduler tokenScheduler;
    private final AppConfig appConfig;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @PostConstruct
    public void init() {
        initializeData();
    }

    private void initializeData() {
        tokenScheduler.fetchNewAccessToken();

        Optional<User> existingUser = userRepository.findByEmail(appConfig.getTestEmail());
        if (existingUser.isEmpty()) {
            var admin = new UserCreateDTO();
            admin.setEmail(appConfig.getTestEmail());
            admin.setFirstName(appConfig.getTestFirstName());
            admin.setLastName(appConfig.getTestLastName());
            admin.setPassword(appConfig.getTestPass());

            userService.create(admin);
            log.info("Admin account created: {}", appConfig.getTestEmail());

        } else {
            log.info("Admin account already exists: {}", appConfig.getTestEmail());
        }
    }
}
