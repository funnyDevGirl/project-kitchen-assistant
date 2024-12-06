package io.project.kitchen_assistant.deserialization;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import java.time.OffsetDateTime;

@Setter
@Getter
public class JsonType {
    @JsonProperty("created_at")
    private OffsetDateTime date;
}
