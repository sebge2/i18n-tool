package be.sgerard.i18n.service.scheduler.executor;

import be.sgerard.i18n.model.scheduler.ScheduledTaskDefinition;
import be.sgerard.i18n.model.scheduler.ScheduledTaskExecutionResult;
import be.sgerard.i18n.service.scheduler.UnsupportedScheduledTaskException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Composite {@link ScheduledTaskExecutor task executor}.
 *
 * @author Sebastien Gerard
 */
@Component
@Primary
public class CompositeScheduledTaskExecutor implements ScheduledTaskExecutor {

    private final List<ScheduledTaskExecutor> executors;

    @Lazy
    public CompositeScheduledTaskExecutor(@Autowired(required = false) List<ScheduledTaskExecutor> executors) {
        this.executors = executors;
    }

    @Override
    public Mono<Boolean> support(String taskDefinitionId) {
        return findProviderByDefinition(taskDefinitionId)
                .hasElement();
    }

    @Override
    public Mono<ScheduledTaskExecutionResult> executeTask(String taskDefinitionId) {
        return findProviderByDefinition(taskDefinitionId)
                .switchIfEmpty(Mono.error(() -> new UnsupportedScheduledTaskException(taskDefinitionId)))
                .flatMap(provider -> provider.executeTask(taskDefinitionId));
    }

    /**
     * Finds the {@link ScheduledTaskExecutor provider} supporting the specified {@link ScheduledTaskDefinition task}.
     */
    private Mono<ScheduledTaskExecutor> findProviderByDefinition(String taskDefinitionId) {
        return Flux.fromIterable(executors)
                .filterWhen(provider -> provider.support(taskDefinitionId))
                .next();
    }
}
