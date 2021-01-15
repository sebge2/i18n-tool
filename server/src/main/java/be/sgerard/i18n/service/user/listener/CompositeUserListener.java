package be.sgerard.i18n.service.user.listener;

import be.sgerard.i18n.model.user.persistence.UserEntity;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Composite {@link UserListener user listener}.
 *
 * @author Sebastien Gerard
 */
@Component
@Primary
public class CompositeUserListener implements UserListener {

    private final List<UserListener> listeners;

    @Lazy
    public CompositeUserListener(List<UserListener> listeners) {
        this.listeners = listeners;
    }

    @Override
    public Mono<Void> afterPersist(UserEntity user) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.afterPersist(user))
                .then();
    }

    @Override
    public Mono<Void> beforeUpdate(UserEntity user) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.beforeUpdate(user))
                .then();
    }

    @Override
    public Mono<Void> afterUpdate(UserEntity user) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.afterUpdate(user))
                .then();
    }

    @Override
    public Mono<Void> beforeDelete(UserEntity user) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.beforeDelete(user))
                .then();
    }

    @Override
    public Mono<Void> afterDelete(UserEntity user) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.afterDelete(user))
                .then();
    }
}
