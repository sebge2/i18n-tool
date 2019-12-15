package be.sgerard.i18n.service.user.listener;

import be.sgerard.i18n.model.security.user.dto.InternalUserCreationDto;
import be.sgerard.i18n.model.security.user.dto.UserPatchDto;
import be.sgerard.i18n.model.security.user.persistence.UserEntity;
import be.sgerard.i18n.model.validation.ValidationMessage;
import be.sgerard.i18n.model.validation.ValidationResult;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.util.Collections.emptyList;

/**
 * {@link UserListener User listener} performing validation of roles.
 *
 * @author Sebastien Gerard
 */
@Component
public class UserRoleValidationListener implements UserListener {

    /**
     * Validation message key specifying that the specified role cannot be assigned.
     */
    public static final String ROLE_NOT_ASSIGNABLE = "validation.user.role-not-assignable";

    public UserRoleValidationListener() {
    }

    @Override
    public Mono<ValidationResult> beforePersist(InternalUserCreationDto info) {
        return Flux.fromIterable(info.getRoles())
                .filter(role -> !role.isAssignableByEndUser())
                .map(role -> ValidationResult.builder().messages(new ValidationMessage(ROLE_NOT_ASSIGNABLE, role.name())).build())
                .reduce(ValidationResult::merge)
                .switchIfEmpty(Mono.just(ValidationResult.EMPTY));
    }

    @Override
    public Mono<ValidationResult> beforeUpdate(UserEntity user, UserPatchDto patch) {
        return Flux.fromIterable(patch.getRoles().orElse(emptyList()))
                .filter(role -> !role.isAssignableByEndUser())
                .map(role -> ValidationResult.builder().messages(new ValidationMessage(ROLE_NOT_ASSIGNABLE, role.name())).build())
                .reduce(ValidationResult::merge)
                .switchIfEmpty(Mono.just(ValidationResult.EMPTY));
    }
}
