package io.project.kitchen_assistant.component;

import io.project.kitchen_assistant.config.ApplicationConstants;
import io.project.kitchen_assistant.repository.StateRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

/**
 * {@code StateCleanupScheduler} - класс, отвечающий за автоматическую очистку
 * устаревших состояний в базе данных.
 *
 * <p>
 * Этот компонент использует механизм планирования задач Spring для
 * периодического удаления устаревших записей из {@link StateRepository}.
 * Задача выполняется с заданным фиксированным интервалом,
 * чтобы поддерживать БД в актуальном состоянии и предотвращать
 * накопление неиспользуемых данных.
 * </p>
 */
@AllArgsConstructor
@Component
@Slf4j
@EnableScheduling
public class StateCleanupScheduler {

    private final StateRepository stateRepository;

    /**
     * Удаляет устаревшие состояния из базы данных.
     *
     * <p>
     * Этот метод вызывается по расписанию, определяемому
     * константой {@link ApplicationConstants#STATE_CLEANUP_RATE_24_HOURS}.
     * Он удаляет записи, срок действия которых истек, основываясь
     * на текущее время {@link LocalDateTime#now()}.
     * </p>
     */
    @Scheduled(fixedRate = ApplicationConstants.STATE_CLEANUP_RATE_24_HOURS)
    public void deleteExpiredStates() {
        LocalDateTime now = LocalDateTime.now();
        stateRepository.deleteByTtlBefore(now);
    }
}
