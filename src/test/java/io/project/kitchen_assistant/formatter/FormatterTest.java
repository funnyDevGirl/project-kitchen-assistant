package io.project.kitchen_assistant.formatter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FormatterTest {
    @ParameterizedTest
    @CsvSource({
            "'*жирный текст* и _курсивный текст_', 'жирный текст и курсивный текст'",
            "'# Заголовок\n* Элемент 1\n* Элемент 2\nЭто абзац', 'Заголовок Элемент 1 Элемент 2 Это абзац'",
            "'', ''",
            "'Только текст без разметки.', 'Только текст без разметки.'",
            "'*Текст с неверной разметкой*', 'Текст с неверной разметкой'",
            "'*жирный текст*\n\n_и курсивный текст_', 'жирный текст и курсивный текст'",
            "'Текст с *особенными* символами, такими как & < >.', 'Текст с особенными символами, такими как & < >.'"
    })
    public void testFormatMarkdownToText(String input, String expectedOutput) {
        String actualOutput = Formatter.formatMarkdownToText(input);
        assertEquals(expectedOutput, actualOutput, "Ошибка в форматировании: " + input);
    }
    @Test
    public void testFormatMarkdownToTextWithSimpleMarkdown() {
        String input = "*жирный текст* и _курсивный текст_";
        String expectedOutput = "жирный текст и курсивный текст";
        String actualOutput = Formatter.formatMarkdownToText(input);
        assertEquals(expectedOutput, actualOutput);
    }
}
