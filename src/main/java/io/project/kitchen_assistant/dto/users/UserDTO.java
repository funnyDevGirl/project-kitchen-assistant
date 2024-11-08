package io.project.kitchen_assistant.dto.users;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Класс для представления существующего пользователя.
 * Содержит идентификатор и информацию о пользователе.
 */
@Getter
@Setter
public class UserDTO {
    private long id;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDate createdAt;
}
