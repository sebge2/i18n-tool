package be.sgerard.i18n.service.user.validation;

import be.sgerard.i18n.model.user.ExternalUser;
import be.sgerard.i18n.model.user.dto.CurrentUserPasswordUpdateDto;
import be.sgerard.i18n.model.user.dto.CurrentUserPatchDto;
import be.sgerard.i18n.model.user.dto.InternalUserCreationDto;
import be.sgerard.i18n.model.user.dto.UserPatchDto;
import be.sgerard.i18n.model.user.persistence.ExternalUserEntity;
import be.sgerard.i18n.model.user.persistence.InternalUserEntity;
import be.sgerard.i18n.model.user.persistence.UserEntity;
import be.sgerard.i18n.model.validation.ValidationResult;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Composite {@link UserValidator user validator}.
 *
 * @author Sebastien Gerard
 */
@Component
@Primary
public class CompositeUserValidator implements UserValidator {

    private final List<UserValidator> validators;

    @Lazy
    public CompositeUserValidator(List<UserValidator> validators) {
        this.validators = validators;
    }

    @Override
    public Mono<ValidationResult> beforePersistOrUpdate(InternalUserEntity user, InternalUserCreationDto creationDto) {
        return Flux
                .fromIterable(validators)
                .flatMap(listener -> listener.beforePersistOrUpdate(user, creationDto))
                .reduce(ValidationResult::merge)
                .switchIfEmpty(Mono.just(ValidationResult.EMPTY));
    }

    @Override
    public Mono<ValidationResult> beforePersistOrUpdate(ExternalUserEntity user, ExternalUser externalUser) {
        return Flux
                .fromIterable(validators)
                .flatMap(listener -> listener.beforePersistOrUpdate(user, externalUser))
                .reduce(ValidationResult::merge)
                .switchIfEmpty(Mono.just(ValidationResult.EMPTY));
    }

    @Override
    public Mono<ValidationResult> beforePersistOrUpdate(UserEntity user) {
        return Flux
                .fromIterable(validators)
                .flatMap(listener -> listener.beforePersistOrUpdate(user))
                .reduce(ValidationResult::merge)
                .switchIfEmpty(Mono.just(ValidationResult.EMPTY));
    }

    @Override
    public Mono<ValidationResult> beforePersistOrUpdate(ExternalUser info) {
        return Flux
                .fromIterable(validators)
                .flatMap(listener -> listener.beforePersistOrUpdate(info))
                .reduce(ValidationResult::merge)
                .switchIfEmpty(Mono.just(ValidationResult.EMPTY));
    }

    @Override
    public Mono<ValidationResult> beforeUpdate(UserEntity user, UserPatchDto patch) {
        return Flux
                .fromIterable(validators)
                .flatMap(listener -> listener.beforeUpdate(user, patch))
                .reduce(ValidationResult::merge)
                .switchIfEmpty(Mono.just(ValidationResult.EMPTY));
    }

    @Override
    public Mono<ValidationResult> beforeUpdate(UserEntity user, CurrentUserPatchDto patch) {
        return Flux
                .fromIterable(validators)
                .flatMap(listener -> listener.beforeUpdate(user, patch))
                .reduce(ValidationResult::merge)
                .switchIfEmpty(Mono.just(ValidationResult.EMPTY));
    }

    @Override
    public Mono<ValidationResult> beforeUpdatePassword(UserEntity user, CurrentUserPasswordUpdateDto update) {
        return Flux
                .fromIterable(validators)
                .flatMap(listener -> listener.beforeUpdatePassword(user, update))
                .reduce(ValidationResult::merge)
                .switchIfEmpty(Mono.just(ValidationResult.EMPTY));
    }

    @Override
    public Mono<ValidationResult> beforeUpdateAvatar(UserEntity user) {
        return Flux
                .fromIterable(validators)
                .flatMap(listener -> listener.beforeUpdateAvatar(user))
                .reduce(ValidationResult::merge)
                .switchIfEmpty(Mono.just(ValidationResult.EMPTY));
    }

    @Override
    public Mono<ValidationResult> beforeDelete(UserEntity user) {
        return Flux
                .fromIterable(validators)
                .flatMap(listener -> listener.beforeDelete(user))
                .reduce(ValidationResult::merge)
                .switchIfEmpty(Mono.just(ValidationResult.EMPTY));
    }
}
