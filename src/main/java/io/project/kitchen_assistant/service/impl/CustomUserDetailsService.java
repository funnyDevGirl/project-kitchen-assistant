package io.project.kitchen_assistant.service.impl;

import io.project.kitchen_assistant.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import static java.lang.String.format;

/**
 * Реализация сервиса для управления деталями пользователя.
 * <p>
 * Этот класс предоставляет методы для загрузки пользовательских деталей из
 * репозитория на основе электронной почты. Он реализует интерфейс UserDetailsManager
 * и включает в себя реализацию необходимых методов, хотя некоторые из них пока не реализованы.
 * </p>
 */
@Slf4j
@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsManager {

    private final UserRepository userRepository;

    /**
     * Загружает детали пользователя по его электронной почте.
     *
     * @param email адрес электронной почты пользователя
     * @return объект UserDetails, представляющий пользователя
     * @throws UsernameNotFoundException если пользователь с указанной электронной почтой не найден
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserDetails userDetails = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(format("User with email '%s' not found", email)));

        log.info("User details loaded: {}", userDetails);
        return userDetails;
    }

    @Generated
    @Override
    public void createUser(UserDetails user) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createUser'");
    }

    @Generated
    @Override
    public void updateUser(UserDetails user) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateUser'");
    }

    @Generated
    @Override
    public void deleteUser(String username) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteUser'");
    }

    @Generated
    @Override
    public void changePassword(String oldPassword, String newPassword) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'changePassword'");
    }

    @Generated
    @Override
    public boolean userExists(String username) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'userExists'");
    }
}
