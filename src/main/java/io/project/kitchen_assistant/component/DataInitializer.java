package io.project.kitchen_assistant.component;

import io.project.kitchen_assistant.dto.users.UserCreateDTO;
import io.project.kitchen_assistant.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {
    private final UserService userService;
    private TokenScheduler tokenScheduler;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        tokenScheduler.fetchNewAccessToken();

        var admin = new UserCreateDTO();
        admin.setEmail("email@example.com");
        admin.setFirstName("Admin");
        admin.setLastName("Admin");
        admin.setPassword("qwerty");
        userService.create(admin);
    }
}
