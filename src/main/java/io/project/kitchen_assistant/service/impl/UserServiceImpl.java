package io.project.kitchen_assistant.service.impl;

import io.project.kitchen_assistant.dto.users.UserCreateDTO;
import io.project.kitchen_assistant.dto.users.UserDTO;
import io.project.kitchen_assistant.dto.users.UserUpdateDTO;
import io.project.kitchen_assistant.exception.UserNotFoundException;
import io.project.kitchen_assistant.mapper.UserMapper;
import io.project.kitchen_assistant.model.User;
import io.project.kitchen_assistant.repository.UserRepository;
import io.project.kitchen_assistant.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import static java.lang.String.format;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDTO create(UserCreateDTO userCreateDTO) {
        User user = userMapper.toUser(userCreateDTO);

        User savedUser = userRepository.save(user);

        log.info("User with email '{}' successfully created", user.getEmail());

        return userMapper.toDto(savedUser);
    }

    public UserDTO update(UserUpdateDTO userUpdateDTO, Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(format("User with id: '%s' not found", id)));

        userMapper.update(userUpdateDTO, user);
        User updatedUser = userRepository.save(user);

        log.info("User with id '{}' successfully updated", user.getId());

        return userMapper.toDto(updatedUser);
    }

    public void delete(Long id) {
        userRepository.findById(id);

        log.info("User with id '{}' successfully deleted", id);
    }

    public UserDTO findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(format("User with id: '%s' not found", id)));

        return userMapper.toDto(user);
    }

    public UserDTO findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(format("User with email: '%s' not found", email)));

        return userMapper.toDto(user);
    }
}
