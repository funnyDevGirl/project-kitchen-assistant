package io.project.kitchen_assistant.model;

import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

/**
 * Класс, содержащий информацию о параметре state,
 * который содержится в запросе на авторизацию в сервисе Todoist.
 *
 * <p>
 * Этот класс хранит информацию о состоянии, включая уникальный идентификатор, статус,
 * время жизни (ttl) и электронную почту пользователя.
 * </p>
 */
@Entity
@Table(name = "states")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class State {
    /**
     * Уникальная и неугадываемая строка. Используется для защиты от CSRF-атак.
     * Значение этого параметра устанавливается при формировании запроса на авторизацию в Todoist.
     * Запрос перенаправления пользователя обратно в приложение также содержит этот параметр.
     * Если значение не соответствует значению запроса в Todoist, значит, он
     * мог быть скомпрометирован другими сторонами, и процесс следует прервать.
     */
    @Id
    @Column(nullable = false, unique = true)
    private String uuid;

    /**
     * Устанавливается при создании uuid.
     * Может иметь 2 значения: "in_progress" и "done".
     */
    @Column(nullable = false)
    private String status;

    /**
     * Используется для обозначения времени жизни State.
     * Параметр используется в классе StateCleanupScheduler.
     */
    @Column(name = "ttl", nullable = false)
    private LocalDateTime ttl;

    /**
     * Электронная почта пользователя, который проходит авторизацию в Todoist.
     */
    @NotBlank
    private String userEmail;
}
