package io.project.kitchen_assistant.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Entity
@Table(name = "states")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class State {
    @Id
    @Column(nullable = false, unique = true)
    private String uuid;

    @Column(nullable = false)
    private String status;

    @Column(name = "ttl", nullable = false)
    private LocalDateTime ttl;
}
