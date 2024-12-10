package io.project.kitchen_assistant.dto.users;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

/**
 * Класс для представления существующего пользователя.
 * Содержит идентификатор и информацию о пользователе.
 */
@Getter
@Setter
@Schema(description = "UserDTO's information")
public class UserDTO {

    @Schema(description = "User's ID", example = "1", type = "long")
    private long id;

    @Schema(description = "User's email", example = "user@gmail.com", type = "string")
    private String email;

    @Schema(description = "User's First Name", example = "Alina", type = "string")
    private String firstName;

    @Schema(description = "User's Last Name", example = "Tarasova", type = "string")
    private String lastName;

    @Schema(description = "Date of created User", example = "2023-01-01", type = "string", format = "date")
    private LocalDate createdAt;
}
