package io.project.kitchen_assistant.dto.recipes.gpt;

import lombok.Data;

/**
 * Класс предназначен только для парсинга ответа от GPT (рецепты).
 */
@Data
public class Usage {
    private String inputTextTokens;
    private String completionTokens;
    private String totalTokens;
}
