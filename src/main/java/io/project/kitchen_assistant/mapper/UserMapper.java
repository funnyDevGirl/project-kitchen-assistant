package io.project.kitchen_assistant.mapper;

import io.project.kitchen_assistant.dto.users.UserDTO;
import io.project.kitchen_assistant.dto.users.UserModificationDTO;
import io.project.kitchen_assistant.model.User;
import org.mapstruct.Mapping;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class UserMapper {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeMapping
    public void encryptPassword(UserModificationDTO modificationDTO) {
        if (StringUtils.hasLength(modificationDTO.getPassword())) {
            var password = modificationDTO.getPassword();
            modificationDTO.setPassword(passwordEncoder.encode(password));
        }
    }

    @Mapping(source = "password", target = "passwordDigest")
    public abstract User toUser(UserModificationDTO modificationDTO);

    public abstract UserDTO toDto(User user);

    @Mapping(target = "passwordDigest", ignore = true)
    public abstract void update(UserModificationDTO modificationDTO, @MappingTarget User user);

    @AfterMapping
    public void setUserFields(UserModificationDTO modificationDTO, @MappingTarget User user) {

        if (StringUtils.hasLength(modificationDTO.getFirstName())) {
            user.setFirstName(modificationDTO.getFirstName());
        }
        if (StringUtils.hasLength(modificationDTO.getLastName())) {
            user.setLastName(modificationDTO.getLastName());
        }
        if (StringUtils.hasLength(modificationDTO.getEmail())) {
            user.setEmail(modificationDTO.getEmail());
        }
    }
}
