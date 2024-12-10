package io.project.kitchen_assistant.dto.users;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@Schema(description = "Information about user authentication in the application")
public class AuthRequest {

    @Schema(description = "User's email", example = "user@gmail.com", type = "string")
    @Email
    private String username;

    @Schema(description = "User's password", example = "qwerty", type = "string")
    @Size(min = 3, max = 100)
    private String password;
}
