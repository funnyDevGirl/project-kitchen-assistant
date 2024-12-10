package io.project.kitchen_assistant.dto.users;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Класс, представляющий собой DTO (Data Transfer Object) для создания
 * и обновления пользователя.
 * В классе используются аннотации валидации с указанием групп.
 * Далее в контроллере будет использоваться аннотация @Validated с указанием группы.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Information for creating or modifying a user")
public class UserModificationDTO {

    @Schema(description = "User's email", example = "user@gmail.com", type = "string")
    @Email(groups = {CreateUserGroup.class})
    @NotBlank(groups = {CreateUserGroup.class})
    private String email;

    @Schema(description = "User's First Name", example = "Alina", type = "string")
    @NotBlank(groups = {CreateUserGroup.class})
    private String firstName;

    @Schema(description = "User's Last Name", example = "Tarasova", type = "string")
    @NotBlank(groups = {CreateUserGroup.class})
    private String lastName;

    @Schema(description = "User's password", example = "qwerty", type = "string")
    @NotBlank(groups = {CreateUserGroup.class})
    @Size(min = 3, groups = {CreateUserGroup.class})
    private String password;
}
