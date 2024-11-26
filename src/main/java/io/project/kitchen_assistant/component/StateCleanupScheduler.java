package io.project.kitchen_assistant.component;

import io.project.kitchen_assistant.config.ApplicationConstants;
import io.project.kitchen_assistant.repository.StateRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@AllArgsConstructor
@Component
@Slf4j
@EnableScheduling
public class StateCleanupScheduler {

    private final StateRepository stateRepository;

    @Scheduled(fixedRate = ApplicationConstants.STATE_CLEANUP_RATE_24_HOURS)
    public void deleteExpiredStates() {
        LocalDateTime now = LocalDateTime.now();
        stateRepository.deleteByTtlBefore(now);
    }
}
