package be.sgerard.test.i18n.mock;

import be.sgerard.i18n.model.scheduler.ScheduledTaskExecutionResult;
import be.sgerard.i18n.service.scheduler.UnsupportedScheduledTaskException;
import be.sgerard.i18n.service.scheduler.executor.ScheduledTaskExecutor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Sebastien Gerard
 */
@Component
public class TestableScheduledTaskExecutor implements ScheduledTaskExecutor {

    private final Map<String, ScheduledTaskExecutionResult> tasks = new HashMap<>();

    public TestableScheduledTaskExecutor() {
    }

    @Override
    public Mono<Boolean> support(String taskDefinitionId) {
        return Mono.just(isSupported(taskDefinitionId));
    }

    @Override
    public Mono<ScheduledTaskExecutionResult> executeTask(String taskDefinitionId) throws UnsupportedScheduledTaskException {
        return Flux
                .fromIterable(tasks.entrySet())
                .filter(entry -> Objects.equals(entry.getKey(), taskDefinitionId))
                .next()
                .map(Map.Entry::getValue)
                .switchIfEmpty(Mono.error(() -> new UnsupportedScheduledTaskException(taskDefinitionId)));
    }

    public void register(String taskDefinitionId, ScheduledTaskExecutionResult result) {
        tasks.put(taskDefinitionId, result);
    }

    public void unregister(String taskDefinitionId) {
        tasks.remove(taskDefinitionId);
    }

    private boolean isSupported(String taskDefinitionId) {
        return tasks.keySet().stream().anyMatch(taskDefinition -> Objects.equals(taskDefinition, taskDefinitionId));
    }
}
