package be.sgerard.i18n.service.user.validator;

import be.sgerard.i18n.model.security.auth.external.ExternalAuthSystem;
import be.sgerard.i18n.model.user.ExternalUser;
import be.sgerard.i18n.model.user.dto.CurrentUserPasswordUpdateDto;
import be.sgerard.i18n.model.user.dto.CurrentUserPatchDto;
import be.sgerard.i18n.model.user.dto.UserPatchDto;
import be.sgerard.i18n.model.user.persistence.InternalUserEntity;
import be.sgerard.i18n.model.user.persistence.UserEntity;
import be.sgerard.i18n.model.validation.ValidationResult;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * @author Sebastien Gerard
 */
@Component
public class UserGlobalInfoValidator implements UserValidator {

    // TODO

    public UserGlobalInfoValidator() {
    }

    @Override
    public Mono<ValidationResult> beforePersist(UserEntity user) {
        return Mono.just(
                ValidationResult.merge(
                        validateNotNullUsername(user.getUsername()),
                        validateNotNullDisplayName(user.getDisplayName()),
                        validateNotNullEmail(user.getEmail())/*,
                        validateNotNullPassword(user.getPassword())*/
                )
        );
    }

    // TODO in before persist
//    @Override
//    public Mono<ValidationResult> beforePersist(ExternalUser info) {
//        return Mono.just(
//                ValidationResult.merge(
//                        validateExternalId(info.getExternalId()),
//                        validateAuthSystem(info.getAuthSystem()),
//                        validateNotNullUsername(info.getUsername()),
//                        validateNotNullDisplayName(info.getDisplayName()),
//                        validateNotNullEmail(info.getEmail())
//                )
//        );
//    }

    @Override
    public Mono<ValidationResult> beforeUpdate(UserEntity user, UserPatchDto patch) {
        if (user instanceof InternalUserEntity) {
            return Mono.just(
                    ValidationResult.merge(
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

    private ValidationResult validateNotNullPassword(String password) {
        return ValidationResult.EMPTY;
    }

    private ValidationResult validateNotNullEmail(String email) {
        return ValidationResult.EMPTY;
    }

    private ValidationResult validateNotNullDisplayName(String displayName) {
        return ValidationResult.EMPTY;
    }

    private ValidationResult validateNotNullUsername(String username) {
        return ValidationResult.EMPTY;
    }

    private ValidationResult validateInternalUser(UserEntity user) {
        return ValidationResult.EMPTY;
    }

    private ValidationResult validateNotAdminUser(UserEntity user) {
        return ValidationResult.EMPTY;
    }

    private ValidationResult validateOnlyRolesUpdated(UserPatchDto patch) {
        return ValidationResult.EMPTY;
    }

    private ValidationResult validateAuthSystem(ExternalAuthSystem authSystem) {
        return ValidationResult.EMPTY;
    }

    private ValidationResult validateExternalId(String externalId) {
        return ValidationResult.EMPTY;
    }
}
