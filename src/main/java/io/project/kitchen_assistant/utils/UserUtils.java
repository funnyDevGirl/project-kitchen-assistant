package io.project.kitchen_assistant.utils;

import io.project.kitchen_assistant.exception.UserNotFoundException;
import io.project.kitchen_assistant.model.User;
import io.project.kitchen_assistant.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import static java.lang.String.format;

/**
 * Утилитный класс для работы с пользователями.
 * <p>
 * Этот класс предоставляет методы для получения информации о
 * текущем пользователе: email, а также для проверки, совпадает ли данный пользователь с аутентифицированным.
 * </p>
 */
@Component
@AllArgsConstructor
public class UserUtils {

    private final UserRepository userRepository;

    /**
     * Получает email текущего аутентифицированного пользователя.
     *
     * @return email текущего пользователя
     * @throws IllegalStateException если пользователь не аутентифицирован
     */
    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {

            return authentication.getName();

        } else {
            throw new IllegalStateException("User is not authenticated");
        }
    }

    /**
     * Проверяет, является ли указанный id пользователя
     * id текущего аутентифицированного пользователя.
     *
     * @param id идентификатор пользователя, который нужно проверить
     * @return true, если идентификатор пользователя соответствует
     *         текущему аутентифицированному пользователю; иначе false
     * @throws UserNotFoundException если пользователь с указанным идентификатором не найден
     * @throws IllegalStateException если пользователь не аутентифицирован
     */
    public boolean isUser(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(format("User with id: '%s' not found", id)));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            throw new IllegalStateException("User is not authenticated");
        }

        String userEmail = user.getEmail();
        String authenticatedEmail = authentication.getName();

        return userEmail.equals(authenticatedEmail);
    }
}
