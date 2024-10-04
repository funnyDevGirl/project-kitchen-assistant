package io.project.kitchen_assistant.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;
import static jakarta.persistence.GenerationType.IDENTITY;


@Entity
@Getter
@AllArgsConstructor
public class Recipe {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String name;
    private List<String> ingredients; // String ?
    private List<String> cookingInstructions; // String ?

}
