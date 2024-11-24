package io.project.kitchen_assistant.service;

import io.project.kitchen_assistant.dto.users.UserCreateDTO;
import io.project.kitchen_assistant.dto.users.UserDTO;

public interface UserService {

    UserDTO create(UserCreateDTO userCreateDTO);

    UserDTO findByEmail(String email);

    String getCurrentUser();
}
