package io.project.kitchen_assistant.dto.recipes.gpt;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

/**
 * Класс предназначен только для парсинга ответа от GPT (рецепты).
 */
@Data
@Schema(description = "Statistics about the usage of the API request")
public class Result {

    @Schema(description = "A list of alternative responses containing messages and statuses",
            example = "[{ \"message\": { \"role\": \"assistant\", \"text\": \"text\" },"
                    + " \"status\": \"ALTERNATIVE_STATUS_FINAL\" }]",
            type = "array")
    @JsonProperty("alternatives")
    private List<Alternative> alternatives;

    @Schema(description = "Statistics about the usage of the API request",
            example = "{ \"inputTextTokens\": \"45\", \"completionTokens\": \"522\", \"totalTokens\": \"567\" }",
            type = "object")
    @JsonProperty("usage")
    private Usage usage;

    @Schema(description = "The model version changes with each new releases",
            example = "23.10.2024", type = "string")
    private String modelVersion;
}
