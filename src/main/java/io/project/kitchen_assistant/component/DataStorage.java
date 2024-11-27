package io.project.kitchen_assistant.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
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

    public String findEmailByData(String data) {
        for (Map.Entry<String, String> entry : storage.entrySet()) {
            if (entry.getValue().equals(data)) {
                log.info("The state's key has been found!");
                return entry.getKey();
            }
        }
        log.info("The key is null!");
        return null;
    }
}
