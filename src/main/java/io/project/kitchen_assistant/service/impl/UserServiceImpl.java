package io.project.kitchen_assistant.service.impl;

import io.project.kitchen_assistant.dto.users.UserCreateDTO;
import io.project.kitchen_assistant.dto.users.UserDTO;
import io.project.kitchen_assistant.exception.UserNotFoundException;
import io.project.kitchen_assistant.mapper.UserMapper;
import io.project.kitchen_assistant.model.User;
import io.project.kitchen_assistant.repository.UserRepository;
import io.project.kitchen_assistant.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDTO create(UserCreateDTO userCreateDTO) {
        User user = userMapper.map(userCreateDTO);
        userRepository.save(user);

        return userMapper.map(user);
    }

    public UserDTO findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with email: '%s' not found", email)));

        return userMapper.map(user);
    }
}
