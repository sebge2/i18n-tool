package be.sgerard.i18n.service.scheduler.executor;

import be.sgerard.i18n.model.scheduler.ScheduledTaskDefinition;
import be.sgerard.i18n.model.scheduler.ScheduledTaskExecutionResult;
import be.sgerard.i18n.model.scheduler.persistence.ScheduledTaskDefinitionEntity;
import be.sgerard.i18n.service.scheduler.UnsupportedScheduledTaskException;
import reactor.core.publisher.Mono;

/**
 * Executor of {@link ScheduledTaskDefinition scheduled tasks}.
 *
 * @author Sebastien Gerard
 */
public interface ScheduledTaskExecutor {

    /**
     * Returns whether the specified {@link ScheduledTaskDefinition task} is supported by this executor.
     *
     * @throws UnsupportedScheduledTaskException if the task is no more supported
     */
    Mono<Boolean> support(String taskDefinitionId);

    /**
     * Executes the {@link ScheduledTaskDefinition task} having the specified id and returns the {@link ScheduledTaskExecutionResult result} of the task.
     *
     * @throws UnsupportedScheduledTaskException if the task is no more supported
     * @see ScheduledTaskDefinitionEntity#getInternalId()
     * @see ScheduledTaskDefinition#getId()
     */
    Mono<ScheduledTaskExecutionResult> executeTask(String taskDefinitionId) throws UnsupportedScheduledTaskException;

}
