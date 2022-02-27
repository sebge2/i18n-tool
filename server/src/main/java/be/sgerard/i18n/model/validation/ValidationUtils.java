package be.sgerard.i18n.model.validation;

import static org.springframework.util.StringUtils.isEmpty;

/**
 * Bunch of utility methods for validation.
 *
 * @author Sebastien Gerard
 */
public final class ValidationUtils {

    private ValidationUtils() {
    }

    /**
     * Asserts that the specified value is not empty (not <tt>null</tt> and not empty string).
     */
    public static ValidationResult assertIsNotEmpty(String value, String messageKey) {
        if (!isEmpty(value)) {
            return ValidationResult.EMPTY;
        }

        return ValidationResult.singleMessage(new ValidationMessage(messageKey));
    }

    /**
     * Asserts that the specified value is not empty (not <tt>null</tt>).
     */
    public static ValidationResult assertIsNotEmpty(Object value, String messageKey) {
        if (!isEmpty(value)) {
            return ValidationResult.EMPTY;
        }

        return ValidationResult.singleMessage(new ValidationMessage(messageKey));
    }
}
