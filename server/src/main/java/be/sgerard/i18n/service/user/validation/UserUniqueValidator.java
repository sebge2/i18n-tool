package be.sgerard.i18n.service.user.validation;

import be.sgerard.i18n.model.user.dto.UserPatchDto;
import be.sgerard.i18n.model.user.persistence.UserEntity;
import be.sgerard.i18n.model.validation.ValidationMessage;
import be.sgerard.i18n.model.validation.ValidationResult;
import be.sgerard.i18n.service.user.UserManager;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * {@link UserValidator Validator} checking that the username is unique among internal users.
 *
 * @author Sebastien Gerard
 */
@Component
public class UserUniqueValidator implements UserValidator {

    /**
     * Validation message key specifying that there is already such username.
     */
    public static final String DUPLICATED_USERNAME = "validation.user.duplicated-username";

    private final UserManager userManager;

    public UserUniqueValidator(UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    public Mono<ValidationResult> beforePersistOrUpdate(UserEntity user) {
        return validateUniqueName(user.getUsername());
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
                .map(existingName -> ValidationResult.singleMessage(new ValidationMessage(DUPLICATED_USERNAME, username)))
                .switchIfEmpty(Mono.just(ValidationResult.EMPTY));
    }
}
