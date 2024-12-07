package io.project.kitchen_assistant.service;

import io.project.kitchen_assistant.model.State;
import io.project.kitchen_assistant.repository.StateRepository;
import io.project.kitchen_assistant.service.impl.StateServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import static io.project.kitchen_assistant.config.ApplicationConstants.STATUS_DONE;
import static io.project.kitchen_assistant.config.ApplicationConstants.STATUS_IN_PROGRESS;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
public class StateServiceImplTest {

    @Mock
    private StateRepository mockStateRepository;

    @InjectMocks
    private StateServiceImpl stateService;

    private String uuid;

    private LocalDateTime ttl;

    private String userEmail;

    private static final long TIME_TOLERANCE_MS = 5;

    @BeforeEach
    public void setUp() {
        uuid = "test-uuid";
        ttl = LocalDateTime.now().plusDays(1);
        userEmail = "test@example.com";
    }

    @Test
    public void testSaveState() {
        // Act
        stateService.saveState(uuid, STATUS_IN_PROGRESS, ttl, userEmail);

        ArgumentCaptor<State> stateCaptor = ArgumentCaptor.forClass(State.class);
        verify(mockStateRepository).save(stateCaptor.capture());

        // Assert
        State capturedState = stateCaptor.getValue();

        assertEquals(uuid, capturedState.getUuid());
        assertEquals(STATUS_IN_PROGRESS, capturedState.getStatus());
        assertEquals(userEmail, capturedState.getUserEmail());

        LocalDateTime expectedTtl = LocalDateTime.now().plusDays(1);
        assertTrue(
                Duration.between(expectedTtl, capturedState.getTtl()).toMillis() <= TIME_TOLERANCE_MS,
                "TTL is outside the expected range"
        );
    }

    @Test
    public void testUpdateStateWhenStateExistsAndInProgress() {
        // Arrange
        State existingState = new State(uuid, STATUS_IN_PROGRESS, ttl, userEmail);
        when(mockStateRepository.findByUuid(uuid)).thenReturn(Optional.of(existingState));

        // Act
        boolean result = stateService.updateState(uuid);

        // Assert
        assertTrue(result);
        verify(mockStateRepository).save(existingState);
        assertEquals(STATUS_DONE, existingState.getStatus());
    }

    @Test
    public void testUpdateStateWhenStateNotFound() {
        // Arrange
        when(mockStateRepository.findByUuid(uuid)).thenReturn(Optional.empty());

        // Act
        boolean result = stateService.updateState(uuid);

        // Assert
        assertFalse(result);
        verify(mockStateRepository, never()).save(any(State.class));
    }

    @Test
    public void testUpdateStateWhenStateExistsButNotInProgress() {
        // Arrange
        String invalidStatus = "completed";
        State existingState = new State(uuid, invalidStatus, ttl, userEmail);
        when(mockStateRepository.findByUuid(uuid)).thenReturn(Optional.of(existingState));

        // Act
        boolean result = stateService.updateState(uuid);

        // Assert
        assertFalse(result);
        verify(mockStateRepository, never()).save(any(State.class));
    }

    @Test
    public void testDeleteState() {
        // Act
        stateService.deleteState(uuid);

        // Assert
        verify(mockStateRepository).deleteByUuid(uuid);
    }
}
