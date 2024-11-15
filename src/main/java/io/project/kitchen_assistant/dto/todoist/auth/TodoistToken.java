package io.project.kitchen_assistant.dto.todoist.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import static java.lang.String.format;

/**
 * Класс для информации о токене, который был получен в Todoist.
 * accessToken - токен доступа к сервису Todoist.
 * tokenType - имеет значение "Bearer".
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class TodoistToken {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType;

    public String generateBearer() {
        return format("%s %s", tokenType, accessToken);
    }
}
