package io.project.kitchen_assistant.deserialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.io.IOException;
import java.time.OffsetDateTime;

@SpringBootTest
public class JacksonLocalDateTimeTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void test() throws IOException {

        final String json = "{ \"created_at\": \"2024-11-02T11:33:17.666272Z\" }";
        final JsonType instance = objectMapper.readValue(json, JsonType.class);

        OffsetDateTime expectedDate = OffsetDateTime.parse("2024-11-02T11:33:17.666272Z");
        Assertions.assertEquals(expectedDate, instance.getDate());
    }
}
