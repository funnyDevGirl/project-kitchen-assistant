package io.project.kitchen_assistant.service;

import io.project.kitchen_assistant.dto.todoist.auth.TodoistToken;

public interface TodoistAuthorizeService {

    String buildAuthUrl(String email);

    TodoistToken exchangeToken(String code, String email);
}
