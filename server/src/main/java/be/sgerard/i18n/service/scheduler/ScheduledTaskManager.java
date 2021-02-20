package be.sgerard.i18n.service.scheduler;

import be.sgerard.i18n.model.scheduler.ScheduledTaskDefinition;
import be.sgerard.i18n.model.scheduler.ScheduledTaskDefinitionSearchRequest;
import be.sgerard.i18n.model.scheduler.dto.ScheduledTaskDefinitionPatchDto;
import be.sgerard.i18n.model.scheduler.persistence.ScheduledTaskDefinitionEntity;
import be.sgerard.i18n.service.ResourceNotFoundException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Manager of {@link ScheduledTaskDefinitionEntity scheduled task}, their definitions and scheduling.
 *
 * @author Sebastien Gerard
 */
public interface ScheduledTaskManager {

    /**
     * Returns the {@link ScheduledTaskDefinitionEntity definition} having the specified id.
     */
    Mono<ScheduledTaskDefinitionEntity> findById(String id);

    /**
     * Returns the {@link ScheduledTaskDefinitionEntity definition} having the specified id.
     */
    default Mono<ScheduledTaskDefinitionEntity> findByIdOrDie(String id) throws ResourceNotFoundException {
        return findById(id)
                .switchIfEmpty(Mono.error(ResourceNotFoundException.scheduledTaskDefinitionNotFoundException(id)));
    }

    /**
     * Returns the {@link ScheduledTaskDefinitionEntity definition} having the specified internal id.
     */
    Mono<ScheduledTaskDefinitionEntity> findByInternalId(String internalId);

    /**
     * Returns the {@link ScheduledTaskDefinitionEntity definition} having the specified internal id.
     */
    default Mono<ScheduledTaskDefinitionEntity> findByInternalIdOrDie(String id) throws ResourceNotFoundException {
        return findByInternalId(id)
                .switchIfEmpty(Mono.error(ResourceNotFoundException.scheduledTaskDefinitionNotFoundException(id)));
    }

    /**
     * Finds {@link ScheduledTaskDefinitionEntity task definitions} satisfying the specified {@link ScheduledTaskDefinitionSearchRequest request}.
     */
    Flux<ScheduledTaskDefinitionEntity> find(ScheduledTaskDefinitionSearchRequest searchRequest);

    /**
     * Finds all {@link ScheduledTaskDefinitionEntity task definitions}.
     */
    default Flux<ScheduledTaskDefinitionEntity> findAll() {
        return find(ScheduledTaskDefinitionSearchRequest.builder().build());
    }

    /**
     * Creates, or updates the specified {@link ScheduledTaskDefinition task}.
     * <p>
     * Be careful:
     * <ul>
     * <li>If the task already exists and it's disabled, it won't be enabled after this call.</li>
     * <li>The trigger configuration won't be updated.</li>
     * <li>Only the task name and description will be updated.</li>
     * </ul>
     */
    Mono<ScheduledTaskDefinitionEntity> createOrUpdate(ScheduledTaskDefinition taskDefinition);

    /**
     * Enables the specified {@link ScheduledTaskDefinitionEntity task} and allow its execution.
     */
    Mono<ScheduledTaskDefinitionEntity> enable(String id);

    /**
     * Disables the specified {@link ScheduledTaskDefinitionEntity task} and don't allow its execution.
     */
    Mono<ScheduledTaskDefinitionEntity> disable(String id);

    /**
     * Updates the {@link ScheduledTaskDefinitionEntity scheduled task definition} using the specified {@link ScheduledTaskDefinitionPatchDto patch}.
     */
    Mono<ScheduledTaskDefinitionEntity> update(ScheduledTaskDefinitionPatchDto patch);

    /**
     * Returns whether the specified {@link ScheduledTaskDefinitionEntity scheduled task definition} is supported.
     */
    Mono<Boolean> isSupported(String id);

    /**
     * Deletes the specified {@link ScheduledTaskDefinitionEntity definition}.
     */
    Mono<Void> delete(String id);
}
