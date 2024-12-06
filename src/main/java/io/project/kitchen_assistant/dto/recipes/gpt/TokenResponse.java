package io.project.kitchen_assistant.dto.recipes.gpt;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class TokenResponse {
    @JsonProperty("iamToken")
    private String iamToken;
    @JsonProperty("expiresAt")
    private OffsetDateTime expiresAt;
}
