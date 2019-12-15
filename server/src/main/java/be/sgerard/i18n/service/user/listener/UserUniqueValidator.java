package be.sgerard.i18n.service.user.listener;

import be.sgerard.i18n.model.security.user.dto.InternalUserCreationDto;
import be.sgerard.i18n.model.security.user.dto.UserPatchDto;
import be.sgerard.i18n.model.security.user.persistence.UserEntity;
import be.sgerard.i18n.model.validation.ValidationMessage;
import be.sgerard.i18n.model.validation.ValidationResult;
import be.sgerard.i18n.service.user.UserManager;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * {@link UserListener Validator} checking that the username is unique among internal users.
 *
 * @author Sebastien Gerard
 */
@Component
public class UserUniqueValidator implements UserListener {

    /**
     * Validation message key specifying that there is already such username.
     */
    public static final String DUPLICATED_USERNAME = "validation.user.duplicated-username";

    private final UserManager userManager;

    public UserUniqueValidator(UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    public Mono<ValidationResult> beforePersist(InternalUserCreationDto info) {
        return validateUniqueName(info.getUsername());
    }

    @Override
    public Mono<ValidationResult> beforeUpdate(UserEntity user, UserPatchDto patch) {
        if (patch.getUsername().isEmpty() || Objects.equals(user.getUsername(), patch.getUsername().get())) {
            return Mono.just(ValidationResult.EMPTY);
        }

        return validateUniqueName(patch.getUsername().get());
    }

    /**
     * Validates that there is no user with such username.
     */
    private Mono<ValidationResult> validateUniqueName(String username) {
        return userManager
                .finUserByName(username)
                .map(existingName -> ValidationResult.builder().messages(new ValidationMessage(DUPLICATED_USERNAME, username)).build())
                .switchIfEmpty(Mono.just(ValidationResult.EMPTY));
    }
}
