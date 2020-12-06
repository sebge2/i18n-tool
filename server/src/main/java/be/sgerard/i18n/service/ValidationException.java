package be.sgerard.i18n.service;

import be.sgerard.i18n.model.validation.ValidationMessage;
import be.sgerard.i18n.model.validation.ValidationResult;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;

/**
 * @author Sebastien Gerard
 */
public class ValidationException extends RuntimeException {

    public static void throwIfFailed(ValidationResult result) {
        if (!result.isSuccessful()) {
            throw new ValidationException(result);
        }
    }

    public static <T> Mono<T> monoSingleMessageValidationError(Supplier<ValidationMessage> supplier){
        return Mono.error(() -> new ValidationException(ValidationResult.singleMessage(supplier.get())));
    }

    private final ValidationResult validationResult;

    public ValidationException(ValidationResult validationResult) {
        this.validationResult = validationResult;
    }

    public ValidationResult getValidationResult() {
        return validationResult;
    }
}
