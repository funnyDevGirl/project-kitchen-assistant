package io.project.kitchen_assistant.component;

import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DataStorage {

    private final ConcurrentHashMap<String, String> storage = new ConcurrentHashMap<>();

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
