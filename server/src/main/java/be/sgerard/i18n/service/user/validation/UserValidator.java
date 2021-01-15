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
import reactor.core.publisher.Mono;

/**
 * Validator of users.
 *
 * @author Sebastien Gerard
 */
public interface UserValidator {

    /**
     * Validates the creation of an internal user.
     */
    default Mono<ValidationResult> beforePersist(InternalUserEntity user, InternalUserCreationDto creationDto) {
        return Mono.just(ValidationResult.EMPTY);
    }

    /**
     * Validates the creation of an internal user.
     */
    default Mono<ValidationResult> beforePersist(ExternalUserEntity user, ExternalUser externalUser) {
        return Mono.just(ValidationResult.EMPTY);
    }

    /**
     * Validates the creation of an internal user.
     */
    default Mono<ValidationResult> beforePersist(UserEntity user) {
        return Mono.just(ValidationResult.EMPTY);
    }

    /**
     * Validates the creation of an internal user.
     */
    default Mono<ValidationResult> beforePersist(ExternalUser info) {
        return Mono.just(ValidationResult.EMPTY);
    }

    /**
     * Validates the update that will be applied on the specified {@link UserEntity user}.
     */
    default Mono<ValidationResult> beforeUpdate(UserEntity user, UserPatchDto patch) {
        return Mono.just(ValidationResult.EMPTY);
    }

    /**
     * Validates the update that will be applied on the specified {@link UserEntity user}.
     */
    default Mono<ValidationResult> beforeUpdate(UserEntity user, CurrentUserPatchDto patch) {
        return Mono.just(ValidationResult.EMPTY);
    }

    /**
     * Validates the password update that will be applied on the specified {@link UserEntity user}.
     */
    default Mono<ValidationResult> beforeUpdatePassword(UserEntity user, CurrentUserPasswordUpdateDto update) {
        return Mono.just(ValidationResult.EMPTY);
    }

    /**
     * Validates the avatar update that will be applied on the specified {@link UserEntity user}.
     */
    default Mono<ValidationResult> beforeUpdateAvatar(UserEntity user) {
        return Mono.just(ValidationResult.EMPTY);
    }

    /**
     * Validates the deletion of the specified entity.
     */
    default Mono<ValidationResult> beforeDelete(UserEntity user) {
        return Mono.just(ValidationResult.EMPTY);
    }
}
