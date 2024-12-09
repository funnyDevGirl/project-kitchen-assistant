package io.project.kitchen_assistant.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * Класс, представляющий пользователя.
 * <p>
 * Этот класс используется для хранения информации о пользователях,
 * включая имя, фамилию, адрес электронной почты, хеш пароля,
 * время создания учетной записи и дополнительный токен для интеграции с Todoist.
 * Также реализует интерфейс {@link UserDetails} для использования в системе аутентификации Spring Security.
 * </p>
 */
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@EqualsAndHashCode(of = "email")
public class User implements UserDetails {

    /**
     * Уникальный идентификатор пользователя.
     * Создается автоматически в базе данных.
     */
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    /**
     * Имя пользователя.
     */
    private String firstName;

    /**
     * Фамилия пользователя.
     */
    private String lastName;

    /**
     * Адрес электронной почты пользователя.
     * Должен соответствовать формату электронной почты и должен быть уникальным в базе данных.
     */
    @Email
    @Column(unique = true)
    private String email;

    /**
     * Хеш пароля пользователя.
     * Должен иметь минимальную длину в 3 символа и не может быть пустым.
     */
    @Size(min = 3)
    @NotBlank
    @JsonIgnore
    private String passwordDigest;

    /**
     * Дата и время создания учетной записи пользователя.
     */
    @CreatedDate
    @Column(name = "created_at")
    private LocalDate createdAt;

    /**
     * Токен для интеграции с Todoist.
     */
    private String todoistToken;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList<GrantedAuthority>();
    }

    @Override
    public String getPassword() {
        return passwordDigest;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
