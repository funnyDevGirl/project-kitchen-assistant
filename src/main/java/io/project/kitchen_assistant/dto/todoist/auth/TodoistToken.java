package io.project.kitchen_assistant.dto.todoist.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "TodoistToken information")
public class TodoistToken {

    @Schema(description = "Access Token from Todoist", example = "12bd608c4648b63ca805224", type = "string")
    @JsonProperty("access_token")
    private String accessToken;

    @Schema(description = "Type for Access Token from Todoist", example = "Bearer", type = "string")
    @JsonProperty("token_type")
    private String tokenType;

    public String generateBearer() {
        return format("%s %s", tokenType, accessToken);
    }
}
