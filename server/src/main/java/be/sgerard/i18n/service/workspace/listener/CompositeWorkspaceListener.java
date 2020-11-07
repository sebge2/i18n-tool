package be.sgerard.i18n.service.workspace.listener;

import be.sgerard.i18n.model.workspace.persistence.WorkspaceEntity;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Composite {@link WorkspaceListener workspace listener}.
 *
 * @author Sebastien Gerard
 */
@Component
@Primary
public class CompositeWorkspaceListener implements WorkspaceListener {

    private final List<WorkspaceListener> listeners;

    @Lazy
    public CompositeWorkspaceListener(List<WorkspaceListener> listeners) {
        this.listeners = listeners;
    }

    @Override
    public boolean support(WorkspaceEntity workspace) {
        return true;
    }

    @Override
    public Mono<Void> afterCreate(WorkspaceEntity workspace) {
        return Flux
                .fromIterable(listeners)
                .filter(listener -> listener.support(workspace))
                .flatMap(listener -> listener.afterCreate(workspace))
                .then();
    }

    @Override
    public Mono<Void> afterInitialize(WorkspaceEntity workspace) {
        return Flux
                .fromIterable(listeners)
                .filter(listener -> listener.support(workspace))
                .flatMap(listener -> listener.afterInitialize(workspace))
                .then();
    }

    @Override
    public Mono<Void> beforeReview(WorkspaceEntity workspace) {
        return Flux
                .fromIterable(listeners)
                .filter(listener -> listener.support(workspace))
                .flatMap(listener -> listener.beforeReview(workspace))
                .then();
    }

    @Override
    public Mono<Void> afterUpdate(WorkspaceEntity workspace) {
        return Flux
                .fromIterable(listeners)
                .filter(listener -> listener.support(workspace))
                .flatMap(listener -> listener.afterUpdate(workspace))
                .then();
    }

    @Override
    public Mono<Void> beforeDelete(WorkspaceEntity workspace) {
        return Flux
                .fromIterable(listeners)
                .filter(listener -> listener.support(workspace))
                .flatMap(listener -> listener.beforeDelete(workspace))
                .then();
    }
}
