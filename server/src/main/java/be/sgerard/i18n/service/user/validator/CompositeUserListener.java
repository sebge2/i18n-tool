package be.sgerard.i18n.service.user.validator;

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
    public Mono<ValidationResult> validateBeforeCreate(UserCreationDto info) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.validateBeforeCreate(info))
                .reduce(ValidationResult::merge)
                .switchIfEmpty(Mono.just(ValidationResult.EMPTY));
    }

    @Override
    public Mono<ValidationResult> validateBeforeUpdate(UserEntity user, UserPatchDto patch) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.validateBeforeUpdate(user, patch))
                .reduce(ValidationResult::merge)
                .switchIfEmpty(Mono.just(ValidationResult.EMPTY));
    }

    @Override
    public Mono<ValidationResult> validateBeforeDelete(UserEntity user) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.validateBeforeDelete(user))
                .reduce(ValidationResult::merge)
                .switchIfEmpty(Mono.just(ValidationResult.EMPTY));
    }
}
