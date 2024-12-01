package io.project.kitchen_assistant.repository;

import io.project.kitchen_assistant.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;


public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    Optional<Recipe> findByName(String name);

    List<Recipe> findAllByUserEmail(String email);

}
