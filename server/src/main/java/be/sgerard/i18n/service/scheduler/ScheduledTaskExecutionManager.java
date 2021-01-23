package be.sgerard.i18n.service.scheduler;

import be.sgerard.i18n.model.scheduler.ScheduledTaskExecution;
import be.sgerard.i18n.model.scheduler.ScheduledTaskExecutionSearchRequest;
import be.sgerard.i18n.model.scheduler.persistence.ScheduledTaskExecutionEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Manager that keeps track of executed scheduled tasks.
 *
 * @author Sebastien Gerard
 */
public interface ScheduledTaskExecutionManager {

    /**
     * Finds all the {@link ScheduledTaskExecutionEntity executions} satisfying the specified {@link ScheduledTaskExecutionSearchRequest request}.
     */
    Flux<ScheduledTaskExecutionEntity> find(ScheduledTaskExecutionSearchRequest request);

    /**
     * Keeps track by persisting the specified {@link ScheduledTaskExecution execution}.
     */
    Mono<ScheduledTaskExecutionEntity> notifyExecution(ScheduledTaskExecution execution);

    /**
     * Deletes the specified {@link ScheduledTaskExecution execution}.
     */
    Mono<Void> delete(ScheduledTaskExecutionEntity execution);
}
