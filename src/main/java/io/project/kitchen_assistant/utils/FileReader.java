package io.project.kitchen_assistant.utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Утилитный класс для чтения файлов ресурсов.
 * <p>
 * Содержит методы для получения абсолютного пути к файлу,
 * а также для чтения содержимого файла в виде строки.
 * Ожидается, что файлы находятся в каталоге "src/main/resources".
 * </p>
 */
public class FileReader {

    /**
     * Получает абсолютный путь к файлу в каталоге ресурсов.
     *
     * @param fileName имя файла, путь к которому необходимо получить
     * @return абсолютный путь к указанному файлу в каталоге ресурсов
     */
    private static Path getFixturePath(String fileName) {
        return Paths.get("src", "main", "resources", fileName)
                .toAbsolutePath().normalize();
    }

    /**
     * Читает содержимое файла ресурсов и возвращает его в виде строки.
     *
     * @param fileName имя файла, который необходимо прочитать
     * @return содержимое указанного файла в виде строки
     * @throws Exception если происходит ошибка при чтении файла,
     *                   например, если файл не найден или недоступен
     */
    public static String readResourceFile(String fileName) throws Exception {
        Path filePath = getFixturePath(fileName);
        return Files.readString(filePath);
    }
}
