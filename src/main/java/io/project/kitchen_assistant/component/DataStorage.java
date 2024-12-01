package io.project.kitchen_assistant.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

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
