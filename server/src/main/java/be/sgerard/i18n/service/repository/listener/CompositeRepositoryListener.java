package be.sgerard.i18n.service.repository.listener;

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
    public Mono<Void> beforePersist(RepositoryEntity repository) {
        return Flux
                .fromIterable(listeners)
                .filter(listener -> listener.support(repository))
                .flatMap(listener -> listener.beforePersist(repository))
                .then();
    }

    @Override
    public Mono<Void> afterPersist(RepositoryEntity repository) {
        return Flux
                .fromIterable(listeners)
                .filter(listener -> listener.support(repository))
                .flatMap(listener -> listener.afterPersist(repository))
                .then();
    }

    @Override
    public Mono<Void> afterInitialize(RepositoryEntity repository) {
        return Flux
                .fromIterable(listeners)
                .filter(listener -> listener.support(repository))
                .flatMap(listener -> listener.afterInitialize(repository))
                .then();
    }

    @Override
    public Mono<Void> beforeUpdate(RepositoryEntity repository) {
        return Flux
                .fromIterable(listeners)
                .filter(listener -> listener.support(repository))
                .flatMap(listener -> listener.beforeUpdate(repository))
                .then();
    }

    @Override
    public Mono<Void> afterUpdate(RepositoryEntity repository) {
        return Flux
                .fromIterable(listeners)
                .filter(listener -> listener.support(repository))
                .flatMap(listener -> listener.afterUpdate(repository))
                .then();
    }

    @Override
    public Mono<Void> beforeDelete(RepositoryEntity repository) {
        return Flux
                .fromIterable(listeners)
                .filter(listener -> listener.support(repository))
                .flatMap(listener -> listener.beforeDelete(repository))
                .then();
    }

    @Override
    public Mono<Void> afterDelete(RepositoryEntity repository) {
        return Flux
                .fromIterable(listeners)
                .filter(listener -> listener.support(repository))
                .flatMap(listener -> listener.afterDelete(repository))
                .then();
    }
}
