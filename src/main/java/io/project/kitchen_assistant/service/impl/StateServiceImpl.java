package io.project.kitchen_assistant.service.impl;

import io.project.kitchen_assistant.model.State;
import io.project.kitchen_assistant.repository.StateRepository;
import io.project.kitchen_assistant.service.StateService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import static io.project.kitchen_assistant.config.ApplicationConstants.STATUS_DONE;
import static io.project.kitchen_assistant.config.ApplicationConstants.STATUS_IN_PROGRESS;

@Slf4j
@AllArgsConstructor
@Service
public class StateServiceImpl implements StateService {

    private final StateRepository stateRepository;

    public void saveState(String uuid, String status, LocalDateTime ttl, String email) {
        State state = new State(uuid, status, LocalDateTime.now().plusDays(1), email);

        stateRepository.save(state);

        log.debug("State '{}' successfully saved", state);
    }

    public boolean updateState(String uuid) {
        Optional<State> optionalState = stateRepository.findByUuid(uuid);

        if (optionalState.isPresent() && STATUS_IN_PROGRESS.equals(optionalState.get().getStatus())) {
            State state = optionalState.get();
            state.setStatus(STATUS_DONE);

            stateRepository.save(state);
            return true;
        }
        return false;
    }

    public void deleteState(String uuid) {
        stateRepository.deleteByUuid(uuid);
    }
}
