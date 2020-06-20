package be.sgerard.i18n.service.user.listener;

import be.sgerard.i18n.model.security.user.dto.ExternalUserDto;
import be.sgerard.i18n.model.security.user.dto.InternalUserCreationDto;
import be.sgerard.i18n.model.security.user.dto.UserPatchDto;
import be.sgerard.i18n.model.security.user.persistence.UserEntity;
import be.sgerard.i18n.model.validation.ValidationResult;
import reactor.core.publisher.Mono;

/**
 * Listener of users.
 *
 * @author Sebastien Gerard
 */
public interface UserListener {

    /**
     * Validates the creation of an internal user.
     */
    default Mono<ValidationResult> beforePersist(InternalUserCreationDto info) {
        return Mono.just(ValidationResult.EMPTY);
    }

    /**
     * Validates the creation of an internal user.
     */
    default Mono<ValidationResult> beforePersist(ExternalUserDto info) {
        return Mono.just(ValidationResult.EMPTY);
    }

    /**
     * Performs an action after the creation of the specified user.
     */
    default Mono<Void> onCreate(UserEntity user){
        return Mono.empty();
    }

    /**
     * Validates the update that will be applied on the specified {@link UserEntity user}.
     */
    default Mono<ValidationResult> beforeUpdate(UserEntity user, UserPatchDto patch) {
        return Mono.just(ValidationResult.EMPTY);
    }

    /**
     * Performs an action after the update of the specified user.
     */
    default Mono<Void> onUpdate(UserEntity user) {
        return Mono.empty();
    }

    /**
     * Validates the deletion of the specified entity.
     */
    default Mono<ValidationResult> beforeDelete(UserEntity user) {
        return Mono.just(ValidationResult.EMPTY);
    }

    /**
     * Performs an action after the deletion of the specified user.
     */
    default Mono<Void> onDelete(UserEntity user) {
        return Mono.empty();
    }
}
