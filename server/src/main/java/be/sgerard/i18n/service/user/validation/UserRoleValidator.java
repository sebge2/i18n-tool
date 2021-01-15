package be.sgerard.i18n.service.user.validation;

import be.sgerard.i18n.model.user.dto.InternalUserCreationDto;
import be.sgerard.i18n.model.user.dto.UserPatchDto;
import be.sgerard.i18n.model.user.persistence.InternalUserEntity;
import be.sgerard.i18n.model.user.persistence.UserEntity;
import be.sgerard.i18n.model.validation.ValidationMessage;
import be.sgerard.i18n.model.validation.ValidationResult;
import be.sgerard.i18n.service.security.UserRole;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

/**
 * {@link UserValidator User listener} performing validation of roles.
 *
 * @author Sebastien Gerard
 */
@Component
public class UserRoleValidator implements UserValidator {

    /**
     * Validation message key specifying that the specified role cannot be assigned.
     */
    public static final String ROLE_NOT_ASSIGNABLE = "validation.user.role-not-assignable";

    public UserRoleValidator() {
    }

    @Override
    public Mono<ValidationResult> beforePersist(InternalUserEntity user, InternalUserCreationDto creationDto) {
        return validateRoles(creationDto.getRoles());
    }

    @Override
    public Mono<ValidationResult> beforeUpdate(UserEntity user, UserPatchDto patch) {
        return patch.getRoles().map(this::validateRoles).orElse(Mono.just(ValidationResult.EMPTY));
    }

    /**
     * Validates the specified {@link UserRole roles}.
     */
    private Mono<ValidationResult> validateRoles(Collection<UserRole> roles) {
        return Flux.fromIterable(roles)
                .filter(role -> !role.isAssignableByEndUser())
                .map(role -> ValidationResult.singleMessage(new ValidationMessage(ROLE_NOT_ASSIGNABLE, role.name())))
                .reduce(ValidationResult::merge)
                .switchIfEmpty(Mono.just(ValidationResult.EMPTY));
    }
}
