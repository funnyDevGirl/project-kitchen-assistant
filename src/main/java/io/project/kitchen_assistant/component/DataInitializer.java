package io.project.kitchen_assistant.component;

import io.project.kitchen_assistant.repository.UserRepository;
import io.project.kitchen_assistant.service.RecipeService;
import io.project.kitchen_assistant.service.UserService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;


@Component
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {
    private UserRepository userRepository;
    private final UserService userService;
    private final RecipeService recipeService;
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Override
    public void run(ApplicationArguments args) throws Exception {

//        var admin = new UserCreateDTO();
//        admin.setEmail("hexlet@example.com");
//        admin.setFirstName("Admin");
//        admin.setLastName("Admin");
//        admin.setPassword("qwerty");
//        userService.create(admin);
    }
}
