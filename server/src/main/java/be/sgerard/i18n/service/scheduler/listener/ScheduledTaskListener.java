package be.sgerard.i18n.service.scheduler.listener;

import be.sgerard.i18n.model.scheduler.persistence.ScheduledTaskDefinitionEntity;
import be.sgerard.i18n.model.scheduler.persistence.ScheduledTaskExecutionEntity;
import reactor.core.publisher.Mono;

/**
 * Listener of the lifecycle of {@link ScheduledTaskDefinitionEntity scheduled task definition}.
 *
 * @author Sebastien Gerard
 */
public interface ScheduledTaskListener {

    /**
     * Performs an action after the specified task definition has been persisted.
     */
    default Mono<Void> afterPersist(ScheduledTaskDefinitionEntity taskDefinition) {
        return Mono.empty();
    }

    /**
     * Performs an action when the specified task definition has been updated.
     */
    default Mono<Void> afterUpdate(ScheduledTaskDefinitionEntity taskDefinition) {
        return Mono.empty();
    }

    /**
     * Performs an action before the deletion of the specified task definition.
     */
    default Mono<Void> beforeDelete(ScheduledTaskDefinitionEntity taskDefinition) {
        return Mono.empty();
    }

    /**
     * Performs an action after the deletion of the specified task definition.
     */
    default Mono<Void> afterDelete(ScheduledTaskDefinitionEntity taskDefinition) {
        return Mono.empty();
    }

    /**
     * Performs an action after execution of a task.
     */
    default Mono<Void> afterExecute(ScheduledTaskExecutionEntity execution){
        return Mono.empty();
    }

    /**
     * Performs an action after the deletion of an execution.
     */
    default Mono<Void> afterDelete(ScheduledTaskExecutionEntity execution){
        return Mono.empty();
    }

}
