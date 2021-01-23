package be.sgerard.i18n.service.scheduler.executor;

import be.sgerard.i18n.configuration.AppProperties;
import be.sgerard.i18n.model.core.localized.LocalizedString;
import be.sgerard.i18n.model.scheduler.ScheduledTaskDefinition;
import be.sgerard.i18n.model.scheduler.ScheduledTaskDefinitionSearchRequest;
import be.sgerard.i18n.model.scheduler.ScheduledTaskExecutionResult;
import be.sgerard.i18n.model.scheduler.ScheduledTaskExecutionSearchRequest;
import be.sgerard.i18n.model.scheduler.persistence.ScheduledTaskDefinitionEntity;
import be.sgerard.i18n.model.scheduler.persistence.ScheduledTaskExecutionEntity;
import be.sgerard.i18n.service.scheduler.ScheduledTaskExecutionManager;
import be.sgerard.i18n.service.scheduler.ScheduledTaskManager;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

import static be.sgerard.i18n.model.scheduler.ScheduledTaskDefinition.recurringTrigger;

/**
 * {@link BaseStaticScheduledTaskExecutor Executor} of the task cleaning old executions and definitions that are no more supported.
 *
 * @author Sebastien Gerard
 */
// NICE: clean up tasks based on the number of tasks
@Component
public class CleanupScheduledTaskExecutor extends BaseStaticScheduledTaskExecutor {

    public static final String TASK_ID = "task-cleanup";

    private final ScheduledTaskManager definitionManager;
    private final ScheduledTaskExecutionManager executionManager;
    private final int cleanupExecutionsOlderThanDays;

    public CleanupScheduledTaskExecutor(ScheduledTaskManager definitionManager,
                                        ScheduledTaskExecutionManager executionManager,
                                        AppProperties appProperties) {
        super(
                ScheduledTaskDefinition.builder()
                        .id(TASK_ID)
                        .name(LocalizedString.fromBundle("i18n/misc", "scheduled-task.cleanup.name"))
                        .description(LocalizedString.fromBundle("i18n/misc", "scheduled-task.cleanup.description"))
                        .trigger(recurringTrigger(appProperties.getScheduledTask().getCleanupFrequency()))
                        .build()
        );

        this.cleanupExecutionsOlderThanDays = appProperties.getScheduledTask().getCleanupExecutionsOlderThanDays();
        this.definitionManager = definitionManager;
        this.executionManager = executionManager;
    }

    @Override
    protected Mono<ScheduledTaskExecutionResult> doExecuteTask() {
        final Instant minimumAge = Instant.now().minus(cleanupExecutionsOlderThanDays, ChronoUnit.DAYS);

        return Mono
                .zip(
                        this::extractNumbers,
                        findOldDefinitions(minimumAge)
                                .map(ScheduledTaskDefinitionEntity::getId)
                                .filterWhen(task -> definitionManager.isSupported(task).map(supported -> !supported))
                                .flatMap(definitionManager::delete)
                                .count(),
                        findOldExecutions(minimumAge)
                                .flatMap(executionManager::delete)
                                .count()
                )
                .map(pair -> createResultMessage(pair.getKey(), pair.getRight()));
    }

    /**
     * Extracts the number of removed definitions and removed executions.
     */
    private Pair<Long, Long> extractNumbers(Object[] objects) {
        if ((objects.length != 2) || !(objects[0] instanceof Long) || !(objects[1] instanceof Long)) {
            throw new IllegalStateException(String.format("Two and only two numbers are expected, but was %s.", Arrays.toString(objects)));
        }

        return Pair.of((Long) objects[0], (Long) objects[1]);
    }

    /**
     * Returns all executions older than the specified time.
     */
    private Flux<ScheduledTaskExecutionEntity> findOldExecutions(Instant minimumAge) {
        return executionManager
                .find(
                        ScheduledTaskExecutionSearchRequest.builder()
                                .executedBeforeOrEqualThan(minimumAge)
                                .build()
                );
    }

    /**
     * Returns all definitions older than the specified time (last execution) and that are inactive.
     */
    private Flux<ScheduledTaskDefinitionEntity> findOldDefinitions(Instant minimumAge) {
        return definitionManager
                .find(
                        ScheduledTaskDefinitionSearchRequest.builder()
                                .executedBeforeOrEqualThan(minimumAge)
                                .build()
                );
    }

    /**
     * Returns a message specifying the number of deleted definitions and executions.
     */
    private ScheduledTaskExecutionResult createResultMessage(long numberDefinitions, long numberExecutions) {
        return ScheduledTaskExecutionResult
                .builder()
                .shortDescription(LocalizedString.fromBundle("i18n/misc", "scheduled-task.cleanup.result.description", numberExecutions, numberDefinitions))
                .build();
    }
}
