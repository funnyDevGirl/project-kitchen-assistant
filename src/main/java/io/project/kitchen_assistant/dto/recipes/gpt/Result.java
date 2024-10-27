package io.project.kitchen_assistant.dto.recipes.gpt;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

/**
 * Класс предназначен только для парсинга ответа от GPT (рецепты).
 */
@Data
public class Result {
    @JsonProperty("alternatives")
    private List<Alternative> alternatives;

    @JsonProperty("usage")
    private Usage usage;
    private String modelVersion;
}
