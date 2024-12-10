package io.project.kitchen_assistant.dto.recipes.gpt;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Класс предназначен только для парсинга ответа от GPT (рецепты).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Statistics about the usage of the API request")
public class Usage {

    @Schema(description = "Number of tokens used in the input text", example = "45", type = "string")
    private String inputTextTokens;

    @Schema(description = "Number of tokens used for the completion", example = "522", type = "string")
    private String completionTokens;

    @Schema(description = "Total number of tokens used in the request", example = "567", type = "string")
    private String totalTokens;
}
