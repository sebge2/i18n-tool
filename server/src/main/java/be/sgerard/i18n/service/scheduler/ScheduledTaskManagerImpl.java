package be.sgerard.i18n.service.scheduler;

import be.sgerard.i18n.model.scheduler.ScheduledTaskDefinition;
import be.sgerard.i18n.model.scheduler.ScheduledTaskDefinitionSearchRequest;
import be.sgerard.i18n.model.scheduler.ScheduledTaskExecution;
import be.sgerard.i18n.model.scheduler.dto.ScheduledTaskDefinitionPatchDto;
import be.sgerard.i18n.model.scheduler.persistence.ScheduledTaskDefinitionEntity;
import be.sgerard.i18n.model.scheduler.persistence.ScheduledTaskExecutionEntity;
import be.sgerard.i18n.repository.scheduler.ScheduledTaskDefinitionRepository;
import be.sgerard.i18n.service.ValidationException;
import be.sgerard.i18n.service.scheduler.executor.ScheduledTaskExecutor;
import be.sgerard.i18n.service.scheduler.executor.StaticScheduledTaskProvider;
import be.sgerard.i18n.service.scheduler.listener.ScheduledTaskListener;
import be.sgerard.i18n.service.scheduler.validation.ScheduledTaskDefinitionValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;

import static be.sgerard.i18n.model.scheduler.ScheduledTaskDefinitionSearchRequest.requestForScheduledDefinitions;

/**
 * Implementation of the {@link ScheduledTaskManager task definition manager}.
 *
 * @author Sebastien Gerard
 */
@Service
public class ScheduledTaskManagerImpl implements ScheduledTaskManager {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTaskManagerImpl.class);

    private final ScheduledTaskDefinitionRepository repository;
    private final StaticScheduledTaskProvider staticTaskProvider;
    private final ScheduledTaskDefinitionValidator validator;
    private final ScheduledTaskListener listener;
    private final ScheduledTaskExecutionManager executionManager;
    private final ScheduledTaskExecutor taskExecutor;
    private final Scheduler scheduler;

    public ScheduledTaskManagerImpl(ScheduledTaskDefinitionRepository repository,
                                    TaskScheduler taskScheduler,
                                    StaticScheduledTaskProvider staticTaskProvider,
                                    ScheduledTaskDefinitionValidator validator,
                                    ScheduledTaskListener listener,
                                    ScheduledTaskExecutionManager executionManager,
                                    ScheduledTaskExecutor taskExecutor) {
        this.repository = repository;
        this.staticTaskProvider = staticTaskProvider;
        this.validator = validator;
        this.listener = listener;
        this.executionManager = executionManager;
        this.taskExecutor = taskExecutor;
        this.scheduler = new Scheduler(taskScheduler, taskExecutor, new SchedulerCallback());
    }

    @Override
    public Mono<ScheduledTaskDefinitionEntity> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public Mono<ScheduledTaskDefinitionEntity> findByInternalId(String internalId) {
        return repository.findByInternalId(internalId);
    }

    @Override
    public Flux<ScheduledTaskDefinitionEntity> find(ScheduledTaskDefinitionSearchRequest searchRequest) {
        return repository.find(searchRequest);
    }

    @Override
    public Mono<ScheduledTaskDefinitionEntity> createOrUpdate(ScheduledTaskDefinition taskDefinition) {
        return Mono
                .just(taskDefinition)
                .flatMap(this::findOrUpdate)
                .flatMap(this::schedule);
    }

    @Override
    public Mono<ScheduledTaskDefinitionEntity> enable(String id) {
        return findByIdOrDie(id)
                .doOnNext(taskDefinition -> taskDefinition.setEnabled(true))
                .flatMap(repository::save)
                .flatMap(this::schedule)
                .flatMap(task -> listener.afterUpdate(task).thenReturn(task));
    }

    @Override
    public Mono<ScheduledTaskDefinitionEntity> disable(String id) {
        return findByIdOrDie(id)
                .doOnNext(taskDefinition -> taskDefinition.setEnabled(false))
                .flatMap(repository::save)
                .flatMap(task -> unschedule(task).thenReturn(task))
                .flatMap(task -> listener.afterUpdate(task).thenReturn(task));
    }

    @Override
    public Mono<ScheduledTaskDefinitionEntity> update(ScheduledTaskDefinitionPatchDto patch) {
        return findByIdOrDie(patch.getId())
                .flatMap(taskDefinition ->
                        validator
                                .beforeUpdate(taskDefinition, patch)
                                .map(validationResult -> {
                                    ValidationException.throwIfFailed(validationResult);

                                    return taskDefinition;
                                })
                )
                .doOnNext(taskDefinition -> applyPatch(taskDefinition, patch))
                .flatMap(this::schedule)
                .flatMap(task -> listener.afterUpdate(task).thenReturn(task));
    }

    @Override
    public Mono<Boolean> isSupported(String id) {
        return taskExecutor.support(id);
    }

    @Override
    public Mono<Void> delete(String id) {
        return findByIdOrDie(id)
                .flatMap(taskDefinition -> unschedule(taskDefinition).thenReturn(taskDefinition))
                .flatMap(taskDefinition -> listener.beforeDelete(taskDefinition).thenReturn(taskDefinition))
                .flatMap(taskDefinition -> repository.delete(taskDefinition).thenReturn(taskDefinition))
                .flatMap(listener::afterDelete);
    }

    /**
     * Initializes the scheduler by loading all known active tasks and static tasks.
     */
    @PostConstruct
    public void init() {
        this
                .find(requestForScheduledDefinitions())
                .filter(ScheduledTaskDefinitionEntity::isEnabled)
                .flatMap(this::schedule)
                .subscribe();

        Flux
                .fromIterable(staticTaskProvider.getTaskDefinitions())
                .flatMap(this::createOrUpdate)
                .subscribe();
    }

    /**
     * Creates or updates the {@link ScheduledTaskDefinitionEntity definition entity} based on its {@link ScheduledTaskDefinition definition}.
     */
    private Mono<ScheduledTaskDefinitionEntity> findOrUpdate(ScheduledTaskDefinition taskDefinition) {
        return repository
                .findByInternalId(taskDefinition.getId())
                .flatMap(entity -> update(entity, taskDefinition))
                .switchIfEmpty(Mono.defer(() -> create(taskDefinition)));
    }

    /**
     * Updates the {@link ScheduledTaskDefinitionEntity definition entity} based on its {@link ScheduledTaskDefinition definition}.
     */
    private Mono<ScheduledTaskDefinitionEntity> update(ScheduledTaskDefinitionEntity definitionEntity, ScheduledTaskDefinition definition) {
        return Mono
                .just(definitionEntity)
                .doOnNext(entity -> {
                    entity.setName(definition.getName());
                    entity.setDescription(definition.getDescription());
                })
                .flatMap(repository::save)
                .flatMap(entity -> listener.afterUpdate(entity).thenReturn(entity));
    }

    /**
     * Creates the {@link ScheduledTaskDefinitionEntity entity} based on the specified definition.
     */
    private Mono<ScheduledTaskDefinitionEntity> create(ScheduledTaskDefinition taskDefinition) {
        return Mono
                .just(new ScheduledTaskDefinitionEntity(taskDefinition))
                .flatMap(taskDef ->
                        validator
                                .beforeCreateOrUpdate(taskDef)
                                .map(validationResult -> {
                                    ValidationException.throwIfFailed(validationResult);

                                    return taskDef;
                                })
                )
                .flatMap(repository::save)
                .doOnNext(taskDef -> logger.info("A new task definition entity [{}] has been recorded with id [{}].", taskDef.getInternalId(), taskDef.getId()))
                .flatMap(task -> listener.afterPersist(task).thenReturn(task));
    }

    /**
     * Schedules the specified {@link ScheduledTaskDefinitionEntity task}.
     */
    private Mono<ScheduledTaskDefinitionEntity> schedule(ScheduledTaskDefinitionEntity taskDefinition) {
        return scheduler
                .schedule(taskDefinition)
                .thenReturn(taskDefinition);
    }

    /**
     * Un-schedules the specified {@link ScheduledTaskDefinitionEntity task}.
     */
    private Mono<Void> unschedule(ScheduledTaskDefinitionEntity taskDefinition) {
        return scheduler.unschedule(taskDefinition);
    }

    /**
     * Updates the {@link ScheduledTaskDefinitionEntity#getLastExecutionTime() time} when the specified {@link ScheduledTaskDefinition definition}
     * was @link ScheduledTaskExecutionEntity executed} for the last time.
     */
    private Mono<ScheduledTaskDefinitionEntity> updateLastExecution(String taskDefinitionId,
                                                                    ScheduledTaskExecutionEntity taskExecution) {
        return this
                .findById(taskDefinitionId)
                .doOnNext(taskDefinitionEntity -> taskDefinitionEntity.setLastExecutionTime(taskExecution.getStartTime()))
                .flatMap(repository::save)
                .flatMap(task -> listener.afterUpdate(task).thenReturn(task));
    }

    /**
     * Applies the specified patch.
     */
    private void applyPatch(ScheduledTaskDefinitionEntity taskDefinition, ScheduledTaskDefinitionPatchDto patch) {
        patch.getTrigger()
                .ifPresent(trigger -> taskDefinition.getTrigger().updateFromDto(trigger));
    }

    /**
     * {@link Scheduler.Callback Callback} persisting updates and recording executions.
     */
    private final class SchedulerCallback implements Scheduler.Callback {

        @Override
        public Mono<ScheduledTaskExecutionEntity> onExecutedTask(ScheduledTaskDefinitionEntity taskDefinition, ScheduledTaskExecution execution) {
            return executionManager
                    .notifyExecution(execution)
                    .flatMap(exec -> updateLastExecution(taskDefinition.getId(), exec).thenReturn(exec));
        }
    }

}
