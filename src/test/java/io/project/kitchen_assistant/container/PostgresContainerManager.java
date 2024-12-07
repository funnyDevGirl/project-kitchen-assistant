package io.project.kitchen_assistant.container;

import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresContainerManager {

    private static final PostgreSQLContainer<?> POSTGRES_CONTAINER =
            new PostgreSQLContainer<>("postgres:latest")
                    .withDatabaseName("test_db")
                    .withUsername("test")
                    .withPassword("test");

    static {
        POSTGRES_CONTAINER.start();
    }

    public static PostgreSQLContainer<?> getContainer() {
        return POSTGRES_CONTAINER;
    }
}
