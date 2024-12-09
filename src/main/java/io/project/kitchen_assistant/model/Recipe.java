package io.project.kitchen_assistant.model;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.util.List;
import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * Класс, представляющий рецепт.
 */
@Entity
@Table(name = "recipes")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Recipe {
    /**
     * Уникальный идентификатор рецепта.
     * Создается автоматически в базе данных.
     */
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    /**
     * Название рецепта.
     * Не может быть пустым.
     */
    @NotBlank
    private String name;

    /**
     * Список ингредиентов, необходимых для приготовления рецепта.
     * Не может быть пустым.
     */
    @NotEmpty(message = "Ingredients must not be empty")
    private List<String> ingredients;

    /**
     * Инструкции по приготовлению рецепта.
     * Максимальная длина - 1000 символов.
     */
    @Size(max = 1000, message = "Instructions must be 1000 characters or less")
    private String instructions;

    /**
     * Пользователь, который создал этот рецепт.
     * Связь с сущностью User.
     */
    @ManyToOne
    private User user;
}
