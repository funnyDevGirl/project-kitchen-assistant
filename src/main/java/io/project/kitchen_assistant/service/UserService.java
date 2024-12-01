package io.project.kitchen_assistant.service;

import io.project.kitchen_assistant.dto.users.UserDTO;
import io.project.kitchen_assistant.dto.users.UserModificationDTO;

public interface UserService {

    UserDTO create(UserModificationDTO modificationDTO);

    UserDTO update(UserModificationDTO modificationDTO, Long id);

    void delete(Long id);

    UserDTO findById(Long id);

    UserDTO findByEmail(String email);
}
