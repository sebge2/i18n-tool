package be.sgerard.i18n.service.user.validator;

import be.sgerard.i18n.model.security.user.dto.UserCreationDto;
import be.sgerard.i18n.model.security.user.dto.UserPatchDto;
import be.sgerard.i18n.model.security.user.persistence.UserEntity;
import be.sgerard.i18n.model.validation.ValidationResult;
import reactor.core.publisher.Mono;

/**
 * Validator of users.
 *
 * @author Sebastien Gerard
 */
public interface UserListener {

    /**
     * Validates the creation of an internal user.
     */
    default Mono<ValidationResult> validateBeforeCreate(UserCreationDto info) {
        return Mono.just(ValidationResult.EMPTY);
    }

    /**
     * Validates the update that will be applied on the specified {@link UserEntity user}.
     */
    default Mono<ValidationResult> validateBeforeUpdate(UserEntity user, UserPatchDto patch) {
        return Mono.just(ValidationResult.EMPTY);
    }

    /**
     * Validates the deletion of the specified entity.
     */
    default Mono<ValidationResult> validateBeforeDelete(UserEntity user) {
        return Mono.just(ValidationResult.EMPTY);
    }
}
