package be.sgerard.i18n.service.repository.listener;

import be.sgerard.i18n.model.repository.dto.RepositoryPatchDto;
import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Composite {@link RepositoryListener repository listener}.
 *
 * @author Sebastien Gerard
 */
@Component
@Primary
public class CompositeRepositoryListener implements RepositoryListener<RepositoryEntity> {

    private final List<RepositoryListener<RepositoryEntity>> listeners;

    @Lazy
    @SuppressWarnings("unchecked")
    public CompositeRepositoryListener(List<RepositoryListener<?>> listeners) {
        this.listeners = (List<RepositoryListener<RepositoryEntity>>) (List<?>) listeners;
    }

    @Override
    public boolean support(RepositoryEntity repositoryEntity) {
        return true;
    }

    @Override
    public Mono<Void> onCreate(RepositoryEntity repository) {
        return Flux
                .fromIterable(listeners)
                .filter(listener -> listener.support(repository))
                .flatMap(listener -> listener.onCreate(repository))
                .then();
    }

    @Override
    public Mono<Void> onInitialize(RepositoryEntity repository) {
        return Flux
                .fromIterable(listeners)
                .filter(listener -> listener.support(repository))
                .flatMap(listener -> listener.onInitialize(repository))
                .then();
    }

    @Override
    public Mono<Void> onInitializationError(RepositoryEntity repository, Throwable error) {
        return Flux
                .fromIterable(listeners)
                .filter(listener -> listener.support(repository))
                .flatMap(listener -> listener.onInitializationError(repository, error))
                .then();
    }

    @Override
    public Mono<Void> onUpdate(RepositoryPatchDto patch, RepositoryEntity repository) {
        return Flux
                .fromIterable(listeners)
                .filter(listener -> listener.support(repository))
                .flatMap(listener -> listener.onUpdate(patch, repository))
                .then();
    }

    @Override
    public Mono<Void> onDelete(RepositoryEntity repository) {
        return Flux
                .fromIterable(listeners)
                .filter(listener -> listener.support(repository))
                .flatMap(listener -> listener.onDelete(repository))
                .then();
    }
}
