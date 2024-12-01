package io.project.kitchen_assistant.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
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
