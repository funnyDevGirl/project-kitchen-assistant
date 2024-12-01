package io.project.kitchen_assistant.dto.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

/**
 * {@code UserUpdateDTO} - Data Transfer Object (DTO) для обновления информации о пользователе.
 *
 * <p>
 * Этот класс используется для передачи данных о пользователе при его обновлении.
 * Он включает в себя необходимые проверки для полей, такие как валидация электронной почты,
 * обязательность имен и пароля, а также минимальная длина пароля.
 * </p>
 */
@Getter
@Setter
public class UserUpdateDTO {

    /**
     * Электронная почта пользователя.
     * Должна иметь корректный формат и не может быть пустой.
     */
    @Email
    @NotBlank
    private JsonNullable<String> email;

    /**
     * Имя пользователя.
     * Не может быть пустым.
     */
    @NotBlank
    private JsonNullable<String> firstName;

    /**
     * Фамилия пользователя.
     * Не может быть пустой.
     */
    @NotBlank
    private JsonNullable<String> lastName;

    /**
     * Пароль пользователя.
     * Не может быть пустым и должен иметь минимальную длину в 3 символа.
     */
    @NotBlank
    @Size(min = 3)
    private JsonNullable<String> password;
}
