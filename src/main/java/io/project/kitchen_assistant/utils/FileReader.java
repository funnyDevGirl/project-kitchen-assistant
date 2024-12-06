package io.project.kitchen_assistant.utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileReader {

    private static Path getFixturePath(String fileName) {
        return Paths.get("src", "main", "resources", fileName)
                .toAbsolutePath().normalize();
    }

    public static String readResourceFile(String fileName) throws Exception {
        Path filePath = getFixturePath(fileName);
        return Files.readString(filePath);
    }
}
