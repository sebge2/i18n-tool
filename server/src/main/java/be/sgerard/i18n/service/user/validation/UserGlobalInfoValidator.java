package be.sgerard.i18n.service.user.validation;

import be.sgerard.i18n.model.security.auth.external.ExternalAuthSystem;
import be.sgerard.i18n.model.user.ExternalUser;
import be.sgerard.i18n.model.user.dto.CurrentUserPasswordUpdateDto;
import be.sgerard.i18n.model.user.dto.CurrentUserPatchDto;
import be.sgerard.i18n.model.user.dto.InternalUserCreationDto;
import be.sgerard.i18n.model.user.dto.UserPatchDto;
import be.sgerard.i18n.model.user.persistence.ExternalUserEntity;
import be.sgerard.i18n.model.user.persistence.InternalUserEntity;
import be.sgerard.i18n.model.user.persistence.UserEntity;
import be.sgerard.i18n.model.validation.ValidationMessage;
import be.sgerard.i18n.model.validation.ValidationResult;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.function.Function;

import static be.sgerard.i18n.support.StringUtils.isEmptyString;

/**
 * {@link UserValidator Validator} of global users' information.
 *
 * @author Sebastien Gerard
 */
@Component
public class UserGlobalInfoValidator implements UserValidator {

    public UserGlobalInfoValidator() {
    }

    @Override
    public Mono<ValidationResult> beforePersistOrUpdate(InternalUserEntity user, InternalUserCreationDto creationDto) {
        return Mono.just(
                ValidationResult.merge(
                        validateNotEmptyUsername(user.getUsername()),
                        validateNotEmptyDisplayName(user.getDisplayName()),
                        validateIfNotAdmin(user,UserEntity::getEmail, this::validateNotEmptyEmail),
                        validateNotEmptyPassword(user.getPassword())
                )
        );
    }

    @Override
    public Mono<ValidationResult> beforePersistOrUpdate(ExternalUserEntity user, ExternalUser externalUser) {
        return Mono.just(
                ValidationResult.merge(
                        validateNotEmptyExternalId(externalUser.getExternalId()),
                        validateNotNullAuthSystem(externalUser.getAuthSystem()),
                        validateNotEmptyUsername(externalUser.getUsername()),
                        validateNotEmptyDisplayName(externalUser.getDisplayName()),
                        validateNotEmptyEmail(externalUser.getEmail())
                )
        );
    }

    @Override
    public Mono<ValidationResult> beforeUpdate(UserEntity user, UserPatchDto patch) {
        if (user instanceof InternalUserEntity) {
            return Mono.just(
                    ValidationResult.merge(
                            patch.getUsername().map(this::validateNotEmptyUsername).orElse(ValidationResult.EMPTY),
                            patch.getDisplayName().map(this::validateNotEmptyDisplayName).orElse(ValidationResult.EMPTY),
                            patch.getEmail().map(this::validateNotEmptyEmail).orElse(ValidationResult.EMPTY),
                            patch.getPassword().map(this::validateNotEmptyPassword).orElse(ValidationResult.EMPTY),

                            validateNotAdminUser(user)
                    )
            );
        } else {
            return Mono.just(
                    ValidationResult.merge(
                            validateOnlyRolesUpdated(patch),
                            validateNotAdminUser(user)
                    )
            );
        }
    }

    @Override
    public Mono<ValidationResult> beforeUpdate(UserEntity user, CurrentUserPatchDto patch) {
        return Mono.just(
                ValidationResult.merge(
                        validateNotAdminUser(user),
                        validateInternalUser(user)
                )
        );
    }

    @Override
    public Mono<ValidationResult> beforeUpdatePassword(UserEntity user, CurrentUserPasswordUpdateDto update) {
        return Mono.just(
                ValidationResult.merge(
                        validateNotEmptyPassword(update.getNewPassword()),
                        validateInternalUser(user)
                )
        );
    }

    @Override
    public Mono<ValidationResult> beforeUpdateAvatar(UserEntity user) {
        return Mono.just(
                ValidationResult.merge(
                        validateNotAdminUser(user),
                        validateInternalUser(user)
                )
        );
    }

    @Override
    public Mono<ValidationResult> beforeDelete(UserEntity user) {
        return Mono.just(
                validateNotAdminUser(user)
        );
    }

    /**
     * Checks that the user name is not empty.
     */
    private ValidationResult validateNotEmptyUsername(String username) {
        if (isEmptyString(username)) {
            return ValidationResult.singleMessage(new ValidationMessage("validation.user.empty-username"));
        }

        return ValidationResult.EMPTY;
    }

    /**
     * Checks that the user display name is not empty.
     */
    private ValidationResult validateNotEmptyDisplayName(String displayName) {
        if (isEmptyString(displayName)) {
            return ValidationResult.singleMessage(new ValidationMessage("validation.user.empty-display-name"));
        }

        return ValidationResult.EMPTY;
    }

    /**
     * Checks that the user's email is not empty.
     */
    private ValidationResult validateNotEmptyEmail(String email) {
        if (isEmptyString(email)) {
            return ValidationResult.singleMessage(new ValidationMessage("validation.user.empty-email"));
        }

        return ValidationResult.EMPTY;
    }

    /**
     * Checks that the user's password is not empty.
     */
    private ValidationResult validateNotEmptyPassword(String password) {
        if (isEmptyString(password)) {
            return ValidationResult.singleMessage(new ValidationMessage("validation.user.empty-password"));
        }

        return ValidationResult.EMPTY;
    }

    /**
     * Checks that the specified user is not the administrator user.
     */
    private ValidationResult validateNotAdminUser(UserEntity user) {
        if (Objects.equals(user.getUsername(), UserEntity.ADMIN_USER_NAME)) {
            return ValidationResult.singleMessage(new ValidationMessage("validation.user.admin-read-only"));
        }

        return ValidationResult.EMPTY;
    }

    /**
     * Checks that only roles have been updated with the specified patch.
     */
    private ValidationResult validateOnlyRolesUpdated(UserPatchDto patch) {
        final boolean modified = patch.getDisplayName().isPresent() || patch.getUsername().isPresent()
                || patch.getEmail().isPresent() || patch.getPassword().isPresent();

        if (modified) {
            return ValidationResult.singleMessage(new ValidationMessage("validation.user.read-only-fields"));
        }

        return ValidationResult.EMPTY;
    }

    /**
     * Checks that the specified user is an internal user.
     */
    private ValidationResult validateInternalUser(UserEntity user) {
        if (!(user instanceof InternalUserEntity)) {
            return ValidationResult.singleMessage(new ValidationMessage("validation.user.not-internal-user"));
        }

        return ValidationResult.EMPTY;
    }

    /**
     * Checks that the specified auth system cannot be null.
     */
    private ValidationResult validateNotNullAuthSystem(ExternalAuthSystem authSystem) {
        if (authSystem == null) {
            return ValidationResult.singleMessage(new ValidationMessage("validation.user.empty-ext-auth-system"));
        }

        return ValidationResult.EMPTY;
    }

    /**
     * Checks that the specified external id cannot be null.
     */
    private ValidationResult validateNotEmptyExternalId(String externalId) {
        if (isEmptyString(externalId)) {
            return ValidationResult.singleMessage(new ValidationMessage("validation.user.empty-external-id"));
        }

        return ValidationResult.EMPTY;
    }

    /**
     * Applies the specified validator only if the specified user is not the administrator.
     */
    private <I> ValidationResult validateIfNotAdmin(InternalUserEntity user,
                                                    Function<InternalUserEntity, I> extractor,
                                                    Function<I, ValidationResult> validator) {
        if(!Objects.equals(user.getUsername(), UserEntity.ADMIN_USER_NAME)){
            return validator.apply(extractor.apply(user));
        }

        return ValidationResult.EMPTY;
    }
}
