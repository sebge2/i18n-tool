package be.sgerard.i18n.service.security.user.validator;

import be.sgerard.i18n.model.security.user.UserCreationDto;
import be.sgerard.i18n.model.security.user.UserEntity;
import be.sgerard.i18n.model.security.user.UserUpdateDto;
import be.sgerard.i18n.model.validation.ValidationResult;

/**
 * Validator of users.
 *
 * @author Sebastien Gerard
 */
public interface UserValidator {

    /**
     * Validates the creation of an internal user.
     */
    default ValidationResult validateOnCreate(UserCreationDto info) {
        return ValidationResult.EMPTY;
    }

    /**
     * Validates the update that will be applied on the specified {@link UserEntity user}.
     */
    default ValidationResult validateOnUpdate(UserUpdateDto userUpdate, UserEntity userEntity) {
        return ValidationResult.EMPTY;
    }

    /**
     * Validates the deletion of the specified entity.
     */
    default ValidationResult validateOnDelete(UserEntity userEntity) {
        return ValidationResult.EMPTY;
    }
}
