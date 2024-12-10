package io.project.kitchen_assistant.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDate;

public class UserTest {

    @Test
    void testEqualsAndHashCode() {
        LocalDate createdAt = LocalDate.now();

        User user1 = new User();
        user1.setId(1L);
        user1.setFirstName("Chuck");
        user1.setLastName("Norris");
        user1.setEmail("chuck.norris@example.com");
        user1.setPasswordDigest("qwerty123");
        user1.setCreatedAt(createdAt);
        user1.setTodoistToken("token123");


        User user2 = new User();
        user2.setId(2L);
        user2.setFirstName("Jane");
        user2.setLastName("Doe");
        user2.setEmail("john.doe@example.com");
        user2.setPasswordDigest("qwerty456");
        user2.setCreatedAt(createdAt);
        user2.setTodoistToken("token456");

        User user3 = new User();
        user3.setId(3L);
        user3.setFirstName("John");
        user3.setLastName("Doe");
        user3.setEmail("john.doe@example.com");
        user3.setPasswordDigest("qwerty789");
        user3.setCreatedAt(createdAt);
        user3.setTodoistToken("token789");


        assertThat(user2).isEqualTo(user3);
        assertThat(user1).isNotEqualTo(user2);

        assertThat(user2.hashCode()).isEqualTo(user3.hashCode());
        assertThat(user1.hashCode()).isNotEqualTo(user2.hashCode());
    }

    @Test
    void testEqualsNull() {
        User user = new User();
        user.setEmail("chuck.norris@example.com");

        assertThat(user).isNotEqualTo(null);
    }

    @Test
    void testEqualsDifferentClass() {
        User user = new User();
        user.setEmail("chuck.norris@example.com");

        assertThat(user).isNotEqualTo(new Object());
    }
}
