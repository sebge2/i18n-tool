package be.sgerard.i18n.service;

import be.sgerard.i18n.model.validation.ValidationResult;

/**
 * @author Sebastien Gerard
 */
public class ValidationException extends RuntimeException {

    public static void throwIfFailed(ValidationResult result){
        if(!result.isSuccessful()){
            throw new ValidationException(result);
        }
    }

    private final ValidationResult validationResult;

    public ValidationException(ValidationResult validationResult) {
        this.validationResult = validationResult;
    }

    public ValidationResult getValidationResult() {
        return validationResult;
    }
}
