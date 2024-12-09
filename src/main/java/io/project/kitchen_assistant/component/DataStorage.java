package io.project.kitchen_assistant.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс для хранения данных, ассоциированных с адресами электронной почты.
 * <p>
 * Этот компонент позволяет сохранять, извлекать и удалять данные, связанные с конкретным
 * адресом электронной почты. Внутри используется карта для хранения пар "email - data".
 * </p>
 *
 * <p>
 * Например, в проекте класс используется для времнного хранения code, который
 * приходит в параметре при авторизации в сервисе Todoist.
 * </p>
 *
 */
@Slf4j
@Component
public class DataStorage {

    private final Map<String, String> storage = new HashMap<>();

    public void save(String email, String data) {
        storage.put(email, data);
    }

    public String get(String email) {
        return storage.get(email);
    }

    public void remove(String email) {
        storage.remove(email);
    }
}
