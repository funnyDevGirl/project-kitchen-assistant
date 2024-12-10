package io.project.kitchen_assistant.dto.recipes.gpt;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.OffsetDateTime;

/**
 * Класс предназначен для получения токена доступа к YandexGPT.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "TokenResponse from YandexGPT information")
public class TokenResponse {

    @Schema(description = "IAM-token for Authentication in the Yandex API ",
            example = "t1.9euelJLju3rnSBF-e856VoicnZW.hJ", type = "string")
    @JsonProperty("iamToken")
    private String iamToken;

    @Schema(description = "The expiration date and time of the IAM token, indicating when the token will"
            + " no longer be valid. The format is ISO 8601", example = "2024-12-10T17:26:52.333292498Z",
            type = "string", format = "date-time")
    @JsonProperty("expiresAt")
    private OffsetDateTime expiresAt;
}
