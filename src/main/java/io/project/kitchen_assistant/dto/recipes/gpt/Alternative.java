package io.project.kitchen_assistant.dto.recipes.gpt;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Класс предназначен только для парсинга ответа от GPT (рецепты).
 */
@Data
@Schema(description = "An alternative response that contains a message and status")
public class Alternative {

    @Schema(description = "Message containing the role and associated content",
            example = "{ \"role\": \"assistant\", \"text\": \"text\" }",
            type = "object")
    private Message message;

    @Schema(description = "Status of the alternative response",
            example = "ALTERNATIVE_STATUS_FINAL", type = "string")
    private String status;
}
