package io.project.kitchen_assistant.controller;

import io.project.kitchen_assistant.dto.users.UserCreateDTO;
import io.project.kitchen_assistant.dto.users.UserDTO;
import io.project.kitchen_assistant.dto.users.UserUpdateDTO;
import io.project.kitchen_assistant.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
public class UsersController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO create(@Valid @RequestBody UserCreateDTO userCreateDTO) {
        return userService.create(userCreateDTO);
    }

    @GetMapping(path = "/current")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO showCurrentUser(Authentication authentication) {
        return userService.findByEmail(authentication.getName());
    }

    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO getById(@PathVariable("id") Long id) {
        return userService.findById(id);
    }

    @PutMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@userUtils.isUser(#id)")
    public UserDTO update(@Valid @RequestBody UserUpdateDTO userUpdateDTO,
                          @PathVariable("id") Long id) {
        return userService.update(userUpdateDTO, id);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@userUtils.isUser(#id)")
    public void delete(@PathVariable("id") Long id) throws Exception {
        userService.delete(id);
    }
}
