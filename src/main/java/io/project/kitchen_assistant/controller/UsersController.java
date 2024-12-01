package io.project.kitchen_assistant.controller;

import io.project.kitchen_assistant.dto.users.CreateUserGroup;
import io.project.kitchen_assistant.dto.users.UpdateUserGroup;
import io.project.kitchen_assistant.dto.users.UserDTO;
import io.project.kitchen_assistant.dto.users.UserModificationDTO;
import io.project.kitchen_assistant.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
public class UsersController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO create(@RequestBody @Validated(CreateUserGroup.class) UserModificationDTO modificationDTO) {
        return userService.create(modificationDTO);
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
    public UserDTO update(@PathVariable("id") Long id,
            @RequestBody @Validated(UpdateUserGroup.class) UserModificationDTO modificationDTO) {
        return userService.update(modificationDTO, id);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@userUtils.isUser(#id)")
    public void delete(@PathVariable("id") Long id) throws Exception {
        userService.delete(id);
    }
}
