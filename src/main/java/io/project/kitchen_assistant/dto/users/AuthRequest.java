package io.project.kitchen_assistant.dto.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class AuthRequest {

    @Email
    private String username;

    @Size(min = 3, max = 100)
    private String password;
}
