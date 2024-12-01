package io.project.kitchen_assistant.repository;

import io.project.kitchen_assistant.model.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.Optional;

public interface StateRepository extends JpaRepository<State, Long> {

    Optional<State> findByUuid(String uuid);

    void deleteByTtlBefore(LocalDateTime now);

    void deleteByUuid(String uuid);
}
