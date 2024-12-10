package io.project.kitchen_assistant.dto.recipes.gpt;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Класс предназначен только для парсинга ответа от GPT (рецепты).
 */
@Data
@Schema(description = "Message containing role and content.")
public class Message {

    @Schema(description = "Role of the message, defines who generated the message", example = "assistant",
            type = "string")
    private String role;

    @Schema(description = "Text of the message, either generated text or other content",
            example = "\"1) **Пирог с вишней**\\n", type = "string")
    private String text;
}
