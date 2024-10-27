package io.project.kitchen_assistant.controller;

import io.project.kitchen_assistant.dto.users.UserCreateDTO;
import io.project.kitchen_assistant.dto.users.UserDTO;
import io.project.kitchen_assistant.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UsersController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO create(@Valid @RequestBody UserCreateDTO userCreateDTO) {
        return userService.create(userCreateDTO);
    }

    @GetMapping(path = "/current")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO show(Authentication authentication) {
        return userService.findByEmail(authentication.getName());
    }
}
