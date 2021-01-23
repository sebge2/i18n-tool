package be.sgerard.i18n.service.scheduler.executor;

import be.sgerard.i18n.model.scheduler.ScheduledTaskDefinition;
import be.sgerard.i18n.model.scheduler.ScheduledTaskExecutionResult;
import be.sgerard.i18n.service.scheduler.UnsupportedScheduledTaskException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Objects;

import static java.util.Collections.singleton;

/**
 * Base implementation of a {@link StaticScheduledTaskProvider static task executor}.
 *
 * @author Sebastien Gerard
 */
public abstract class BaseStaticScheduledTaskExecutor implements StaticScheduledTaskProvider, ScheduledTaskExecutor {

    private final ScheduledTaskDefinition taskDefinition;

    protected BaseStaticScheduledTaskExecutor(ScheduledTaskDefinition taskDefinition) {
        this.taskDefinition = taskDefinition;
    }

    @Override
    public final Collection<ScheduledTaskDefinition> getTaskDefinitions() {
        return singleton(taskDefinition);
    }

    @Override
    public final Mono<Boolean> support(String taskDefinitionId) {
        return Flux.fromIterable(getTaskDefinitions())
                .filter(taskDefinition -> Objects.equals(taskDefinitionId, taskDefinition.getId()))
                .hasElements();
    }

    @Override
    public final Mono<ScheduledTaskExecutionResult> executeTask(String taskDefinitionId) throws UnsupportedScheduledTaskException {
        if (!Objects.equals(taskDefinitionId, taskDefinition.getId())) {
            return Mono.error(() -> new UnsupportedScheduledTaskException(taskDefinitionId));
        }

        return doExecuteTask();
    }

    /**
     * Executes the specified {@link ScheduledTaskDefinition task}.
     */
    protected abstract Mono<ScheduledTaskExecutionResult> doExecuteTask();
}
