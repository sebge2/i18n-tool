package be.sgerard.i18n.service.user.listener;

import be.sgerard.i18n.model.security.user.dto.UserCreationDto;
import be.sgerard.i18n.model.security.user.dto.UserPatchDto;
import be.sgerard.i18n.model.security.user.persistence.UserEntity;
import be.sgerard.i18n.model.validation.ValidationResult;
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
    public Mono<ValidationResult> beforePersist(UserCreationDto info) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.beforePersist(info))
                .reduce(ValidationResult::merge)
                .switchIfEmpty(Mono.just(ValidationResult.EMPTY));
    }

    @Override
    public Mono<Void> onCreate(UserEntity user) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.onCreate(user))
                .then();
    }

    @Override
    public Mono<ValidationResult> beforeUpdate(UserEntity user, UserPatchDto patch) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.beforeUpdate(user, patch))
                .reduce(ValidationResult::merge)
                .switchIfEmpty(Mono.just(ValidationResult.EMPTY));
    }

    @Override
    public Mono<Void> onUpdate(UserEntity user) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.onUpdate(user))
                .then();
    }

    @Override
    public Mono<ValidationResult> beforeDelete(UserEntity user) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.beforeDelete(user))
                .reduce(ValidationResult::merge)
                .switchIfEmpty(Mono.just(ValidationResult.EMPTY));
    }

    @Override
    public Mono<Void> onDelete(UserEntity user) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.onDelete(user))
                .then();
    }
}
