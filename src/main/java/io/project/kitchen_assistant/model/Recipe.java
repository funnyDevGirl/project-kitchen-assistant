package io.project.kitchen_assistant.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "recipes")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Recipe {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String name;
    @Size(max = 255, message = "Ingredients must be 255 characters or less")
    private String ingredients;

    @Size(max = 1000, message = "Instructions must be 1000 characters or less")
    private String instructions;

    @ManyToOne
    private User user;
}
