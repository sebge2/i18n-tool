package be.sgerard.i18n.service.workspace.listener;

import be.sgerard.i18n.model.validation.ValidationResult;
import be.sgerard.i18n.model.workspace.persistence.WorkspaceEntity;
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

    public CompositeWorkspaceListener(List<WorkspaceListener> listeners) {
        this.listeners = listeners;
    }

    @Override
    public boolean support(WorkspaceEntity workspace) {
        return true;
    }

    @Override
    public Mono<Void> onCreate(WorkspaceEntity workspace) {
        return Flux
                .fromIterable(listeners)
                .filter(listener -> listener.support(workspace))
                .flatMap(listener -> listener.onCreate(workspace))
                .then();
    }

    @Override
    public Mono<ValidationResult> beforeInitialize(WorkspaceEntity workspace) {
        return Flux
                .fromIterable(listeners)
                .filter(listener -> listener.support(workspace))
                .flatMap(listener -> listener.beforeInitialize(workspace))
                .reduce(ValidationResult::merge)
                .switchIfEmpty(Mono.just(ValidationResult.EMPTY));
    }

    @Override
    public Mono<Void> onInitialize(WorkspaceEntity workspace) {
        return Flux
                .fromIterable(listeners)
                .filter(listener -> listener.support(workspace))
                .flatMap(listener -> listener.onInitialize(workspace))
                .then();
    }

    @Override
    public Mono<ValidationResult> beforePublish(WorkspaceEntity workspace) {
        return Flux
                .fromIterable(listeners)
                .filter(listener -> listener.support(workspace))
                .flatMap(listener -> listener.beforePublish(workspace))
                .reduce(ValidationResult::merge)
                .switchIfEmpty(Mono.just(ValidationResult.EMPTY));
    }

    @Override
    public Mono<Void> onReview(WorkspaceEntity workspace) {
        return Flux
                .fromIterable(listeners)
                .filter(listener -> listener.support(workspace))
                .flatMap(listener -> listener.onReview(workspace))
                .then();
    }

    @Override
    public Mono<ValidationResult> beforeFinishReview(WorkspaceEntity workspace) {
        return Flux
                .fromIterable(listeners)
                .filter(listener -> listener.support(workspace))
                .flatMap(listener -> listener.beforeFinishReview(workspace))
                .reduce(ValidationResult::merge)
                .switchIfEmpty(Mono.just(ValidationResult.EMPTY));
    }

    @Override
    public Mono<ValidationResult> beforeUpdate(WorkspaceEntity workspace) {
        return Flux
                .fromIterable(listeners)
                .filter(listener -> listener.support(workspace))
                .flatMap(listener -> listener.beforeUpdate(workspace))
                .reduce(ValidationResult::merge)
                .switchIfEmpty(Mono.just(ValidationResult.EMPTY));
    }

    @Override
    public Mono<Void> onUpdate(WorkspaceEntity workspace) {
        return Flux
                .fromIterable(listeners)
                .filter(listener -> listener.support(workspace))
                .flatMap(listener -> listener.onUpdate(workspace))
                .then();
    }

    @Override
    public Mono<Void> onDelete(WorkspaceEntity workspace) {
        return Flux
                .fromIterable(listeners)
                .filter(listener -> listener.support(workspace))
                .flatMap(listener -> listener.onDelete(workspace))
                .then();
    }
}
