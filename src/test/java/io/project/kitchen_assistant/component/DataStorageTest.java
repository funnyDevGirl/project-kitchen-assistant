package io.project.kitchen_assistant.component;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataStorageTest {

    private DataStorage dataStorage;

    @BeforeEach
    void setUp() {
        dataStorage = new DataStorage();
    }

    @Test
    void testSaveAndGet() {
        String email = "test@example.com";
        String data = "Test Data";

        dataStorage.save(email, data);

        String retrievedData = dataStorage.get(email);
        assertEquals(data, retrievedData, "Данные, полученные из dataStorage идентичны сохраненным данным");
    }

    @Test
    void testGetNonExistent() {
        String email = "nonexistent@example.com";

        String retrievedData = dataStorage.get(email);
        assertNull(retrievedData, "Данные для несуществующего email должны быть null");
    }

    @Test
    void testRemove() {
        String email = "test@example.com";
        String data = "Test Data";

        dataStorage.save(email, data);
        dataStorage.remove(email);

        String retrievedData = dataStorage.get(email);
        assertNull(retrievedData, "Когда удалила данные и хочу их получить, ловлю null");
    }

    @Test
    void testRemoveNonExistent() {
        String email = "nonexistent@example.com";

        assertDoesNotThrow(() -> dataStorage.remove(email), "Удаляю несуществующий email и всё норм");
    }
}
