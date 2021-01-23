package be.sgerard.i18n.service.scheduler.listener;

import be.sgerard.i18n.model.scheduler.ScheduledTaskExecutionSearchRequest;
import be.sgerard.i18n.model.scheduler.persistence.ScheduledTaskDefinitionEntity;
import be.sgerard.i18n.service.scheduler.ScheduledTaskExecutionManager;
import reactor.core.publisher.Mono;

/**
 * {@link ScheduledTaskListener Listener} removing all executions of deleted tasks.
 *
 * @author Sebastien Gerard
 */
public class ScheduledTaskDeletionListener implements ScheduledTaskListener {

    private final ScheduledTaskExecutionManager executionManager;

    public ScheduledTaskDeletionListener(ScheduledTaskExecutionManager executionManager) {
        this.executionManager = executionManager;
    }

    @Override
    public Mono<Void> beforeDelete(ScheduledTaskDefinitionEntity taskDefinition) {
        final ScheduledTaskExecutionSearchRequest searchRequest = ScheduledTaskExecutionSearchRequest.builder()
                .taskDefinitionId(taskDefinition.getId())
                .build();

        return executionManager
                .find(searchRequest)
                .flatMap(executionManager::delete)
                .then();
    }
}
