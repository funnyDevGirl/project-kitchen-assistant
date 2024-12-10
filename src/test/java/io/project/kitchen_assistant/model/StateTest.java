package io.project.kitchen_assistant.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDateTime;

public class StateTest {
    @Test
    void testEqualsAndHashCode() {
        LocalDateTime ttl = LocalDateTime.now();

        State state1 = new State("123-456-789", "in_progress", ttl, "test@example.com");
        State state2 = new State("123-456-789", "in_progress", ttl, "test@example.com");
        State state3 = new State("987-654-321", "done", ttl, "test@example.com");

        assertThat(state1).isEqualTo(state2);
        assertThat(state1).isNotEqualTo(state3);

        assertThat(state1.hashCode()).isEqualTo(state2.hashCode());
        assertThat(state1.hashCode()).isNotEqualTo(state3.hashCode());
    }

    @Test
    void testEqualsNull() {
        State state = new State("123-456-789", "in_progress", LocalDateTime.now(), "test@example.com");

        assertThat(state).isNotEqualTo(null);
    }

    @Test
    void testEqualsDifferentClass() {
        State state = new State("123-456-789", "in_progress", LocalDateTime.now(), "test@example.com");

        assertThat(state).isNotEqualTo(new Object());
    }
}
