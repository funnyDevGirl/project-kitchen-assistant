package io.project.kitchen_assistant.dto.recipes.gpt;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Класс предназначен только для парсинга ответа от GPT (рецепты).
 */
@Data
public class Alternative {
    @JsonProperty("message")
    private Message message;
    private String status;
}
