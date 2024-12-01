package io.project.kitchen_assistant.dto.users;

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
public class UserModificationDTO {

    @Email(groups = {CreateUserGroup.class})
    @NotBlank(groups = {CreateUserGroup.class})
    private String email;

    @NotBlank(groups = {CreateUserGroup.class})
    private String firstName;

    @NotBlank(groups = {CreateUserGroup.class})
    private String lastName;

    @NotBlank(groups = {CreateUserGroup.class})
    @Size(min = 3, groups = {CreateUserGroup.class})
    private String password;
}
