package be.sgerard.i18n.service.workspace.validation;

import be.sgerard.i18n.model.validation.ValidationResult;
import be.sgerard.i18n.model.workspace.persistence.WorkspaceEntity;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Composite {@link WorkspaceValidator workspace validator}.
 *
 * @author Sebastien Gerard
 */
@Component
@Primary
public class CompositeWorkspaceValidator implements WorkspaceValidator {

    private final List<WorkspaceValidator> listeners;

    public CompositeWorkspaceValidator(List<WorkspaceValidator> listeners) {
        this.listeners = listeners;
    }

    @Override
    public boolean support(WorkspaceEntity workspace) {
        return true;
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
    public Mono<ValidationResult> beforePublish(WorkspaceEntity workspace) {
        return Flux
                .fromIterable(listeners)
                .filter(listener -> listener.support(workspace))
                .flatMap(listener -> listener.beforePublish(workspace))
                .reduce(ValidationResult::merge)
                .switchIfEmpty(Mono.just(ValidationResult.EMPTY));
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
}
