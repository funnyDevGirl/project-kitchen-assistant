package io.project.kitchen_assistant.service;

import io.project.kitchen_assistant.dto.users.UserCreateDTO;
import io.project.kitchen_assistant.dto.users.UserDTO;
import io.project.kitchen_assistant.dto.users.UserUpdateDTO;

public interface UserService {

    UserDTO create(UserCreateDTO userCreateDTO);

    UserDTO update(UserUpdateDTO userUpdateDTO, Long id);

    void delete(Long id);

    UserDTO findById(Long id);

    UserDTO findByEmail(String email);
}
