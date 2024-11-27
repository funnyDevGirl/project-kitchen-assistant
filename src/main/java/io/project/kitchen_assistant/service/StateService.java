package io.project.kitchen_assistant.service;

import java.time.LocalDateTime;

public interface StateService {

    void saveState(String uuid, String status, LocalDateTime ttl, String email);

    boolean updateState(String uuid);

    void deleteState(String uuid);
}
