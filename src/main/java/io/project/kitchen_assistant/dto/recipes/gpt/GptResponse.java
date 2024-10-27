package io.project.kitchen_assistant.dto.recipes.gpt;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Класс предназначен только для парсинга ответа от GPT (рецепты).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GptResponse {

    @JsonProperty("result")
    private Result result;
}
