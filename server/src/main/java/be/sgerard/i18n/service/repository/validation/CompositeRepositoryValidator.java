package be.sgerard.i18n.service.repository.validation;

import be.sgerard.i18n.model.repository.dto.RepositoryPatchDto;
import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.model.validation.ValidationResult;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Composite {@link RepositoryValidator repository validator}.
 *
 * @author Sebastien Gerard
 */
@Component
@Primary
public class CompositeRepositoryValidator implements RepositoryValidator<RepositoryEntity> {

    private final List<RepositoryValidator<RepositoryEntity>> listeners;

    @Lazy
    @SuppressWarnings("unchecked")
    public CompositeRepositoryValidator(List<RepositoryValidator<?>> listeners) {
        this.listeners = (List<RepositoryValidator<RepositoryEntity>>) (List<?>) listeners;
    }

    @Override
    public boolean support(RepositoryEntity repositoryEntity) {
        return true;
    }

    @Override
    public Mono<ValidationResult> beforePersist(RepositoryEntity repository) {
        return Flux
                .fromIterable(listeners)
                .filter(listener -> listener.support(repository))
                .flatMap(listener -> listener.beforePersist(repository))
                .reduce(ValidationResult::merge)
                .switchIfEmpty(Mono.just(ValidationResult.EMPTY));
    }

    @Override
    public Mono<ValidationResult> beforeUpdate(RepositoryEntity original, RepositoryPatchDto patch) {
        return Flux
                .fromIterable(listeners)
                .filter(listener -> listener.support(original))
                .flatMap(listener -> listener.beforeUpdate(original, patch))
                .reduce(ValidationResult::merge)
                .switchIfEmpty(Mono.just(ValidationResult.EMPTY));
    }

    @Override
    public Mono<ValidationResult> beforeDelete(RepositoryEntity repository) {
        return Flux
                .fromIterable(listeners)
                .filter(listener -> listener.support(repository))
                .flatMap(listener -> listener.beforeDelete(repository))
                .reduce(ValidationResult::merge)
                .switchIfEmpty(Mono.just(ValidationResult.EMPTY));
    }
}
