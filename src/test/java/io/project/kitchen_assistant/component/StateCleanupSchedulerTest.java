package io.project.kitchen_assistant.component;

import io.project.kitchen_assistant.repository.StateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

public class StateCleanupSchedulerTest {

    @Mock
    private StateRepository stateRepository;

    @InjectMocks
    private StateCleanupScheduler stateCleanupScheduler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testDeleteExpiredStates() {
        stateCleanupScheduler.deleteExpiredStates();

        ArgumentCaptor<LocalDateTime> captor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(stateRepository).deleteByTtlBefore(captor.capture());

        LocalDateTime capturedDateTime = captor.getValue();

        LocalDateTime now = LocalDateTime.now();
        assertTrue(now.minusSeconds(1).isBefore(capturedDateTime) && now.plusSeconds(1).isAfter(capturedDateTime),
                "Значение во времени должно быть близким к текущему времени");
    }
}
