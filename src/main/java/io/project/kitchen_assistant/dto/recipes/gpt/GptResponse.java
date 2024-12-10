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
@Schema(description = "Response from the API containing alternatives and usage information")
public class GptResponse {

    @Schema(description = "The response containing the result of the API call, "
            + "including usage statistics and alternative responses",
            example = "{ \"alternatives\": [{ \"message\": { \"role\": \"assistant\", \"text\": \"text\" }, "
                    + "\"status\": \"ALTERNATIVE_STATUS_FINAL\" }], \"usage\": { \"inputTextTokens\": \"45\", "
                    + "\"completionTokens\": \"522\", \"totalTokens\": \"567\" }, \"modelVersion\": \"23.10.2024\" }",
            type = "object")
    private Result result;
}
