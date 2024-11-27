package io.project.kitchen_assistant.service;

import io.project.kitchen_assistant.dto.todoist.auth.TodoistToken;

public interface TodoistAuthorizeService {

    String buildAuthUrl();

    TodoistToken exchangeToken(String code, String email);
}
