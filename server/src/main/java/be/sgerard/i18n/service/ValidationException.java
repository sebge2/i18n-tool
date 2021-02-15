package be.sgerard.i18n.service;

import be.sgerard.i18n.model.core.localized.LocalizedString;
import be.sgerard.i18n.model.error.LocalizedMessagesHolder;
import be.sgerard.i18n.model.validation.ValidationMessage;
import be.sgerard.i18n.model.validation.ValidationResult;
import lombok.Getter;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Supplier;

/**
 * Exception thrown when a validation failed .
 *
 * @author Sebastien Gerard
 */
@Getter
public class ValidationException extends RuntimeException implements LocalizedMessagesHolder {

    public static void throwIfFailed(ValidationResult result) {
        if (!result.isSuccessful()) {
            throw new ValidationException(result);
        }
    }

    public static <T> Mono<T> monoSingleMessageValidationError(Supplier<ValidationMessage> supplier) {
        return Mono.error(() -> new ValidationException(ValidationResult.singleMessage(supplier.get())));
    }

    /**
     * {@link ValidationResult Result} that failed.
     */
    private final ValidationResult validationResult;

    public ValidationException(ValidationResult validationResult) {
        this.validationResult = validationResult;
    }

    @Override
    public List<LocalizedString> toLocalizedMessages() {
        return validationResult.toLocalizedMessages();
    }
}
