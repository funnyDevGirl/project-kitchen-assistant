package io.project.kitchen_assistant.mapper;

import io.project.kitchen_assistant.dto.users.UserCreateDTO;
import io.project.kitchen_assistant.dto.users.UserDTO;
import io.project.kitchen_assistant.model.User;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;


@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class UserMapper {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeMapping
    public void encryptPassword(UserCreateDTO userCreateDTO) {
        String password = userCreateDTO.getPassword();
        userCreateDTO.setPassword(passwordEncoder.encode(password));
    }

    @Mapping(source = "password", target = "passwordDigest")
    public abstract User map(UserCreateDTO userCreateDTO);

    public abstract UserDTO map(User user);
}
