package io.project.kitchen_assistant.deserialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.OffsetDateTime;
import java.util.Map;

@SpringBootTest
public class JacksonConfigTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void test() {

        Map<String, String> jsonMap = Map.of("created_at", "2024-11-02T11:33:17.666272Z");

        JsonType instance = objectMapper.convertValue(jsonMap, JsonType.class);

        OffsetDateTime expectedDate = OffsetDateTime.parse("2024-11-02T11:33:17.666272Z");
        Assertions.assertEquals(expectedDate, instance.getDate());
    }
}
