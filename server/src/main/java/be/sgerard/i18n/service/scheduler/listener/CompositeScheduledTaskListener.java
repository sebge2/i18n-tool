package be.sgerard.i18n.service.scheduler.listener;

import be.sgerard.i18n.model.scheduler.persistence.ScheduledTaskDefinitionEntity;
import be.sgerard.i18n.model.scheduler.persistence.ScheduledTaskExecutionEntity;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Composite {@link ScheduledTaskListener scheduled task listener}.
 *
 * @author Sebastien Gerard
 */
@Primary
@Component
public class CompositeScheduledTaskListener implements ScheduledTaskListener {

    private final List<ScheduledTaskListener> listeners;

    @Lazy
    public CompositeScheduledTaskListener(List<ScheduledTaskListener> listeners) {
        this.listeners = listeners;
    }

    @Override
    public Mono<Void> afterPersist(ScheduledTaskDefinitionEntity taskDefinition) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.afterPersist(taskDefinition))
                .then();
    }

    @Override
    public Mono<Void> afterUpdate(ScheduledTaskDefinitionEntity taskDefinition) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.afterUpdate(taskDefinition))
                .then();
    }

    @Override
    public Mono<Void> beforeDelete(ScheduledTaskDefinitionEntity taskDefinition) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.beforeDelete(taskDefinition))
                .then();
    }

    @Override
    public Mono<Void> afterDelete(ScheduledTaskDefinitionEntity taskDefinition) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.afterDelete(taskDefinition))
                .then();
    }

    @Override
    public Mono<Void> afterExecute(ScheduledTaskExecutionEntity execution) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.afterExecute(execution))
                .then();
    }

    @Override
    public Mono<Void> afterDelete(ScheduledTaskExecutionEntity execution) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.afterDelete(execution))
                .then();
    }
}
