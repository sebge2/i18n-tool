package be.sgerard.i18n.service.locale.validation;

import be.sgerard.i18n.model.i18n.dto.TranslationLocaleDto;
import be.sgerard.i18n.model.locale.persistence.TranslationLocaleEntity;
import be.sgerard.i18n.model.validation.ValidationResult;
import reactor.core.publisher.Mono;

/**
 * Validator of {@link TranslationLocaleEntity translation locales}.
 *
 * @author Sebastien Gerard
 */
public interface TranslationLocaleValidator {

    /**
     * Validates before persisting the specified {@link TranslationLocaleEntity locale}.
     */
    default Mono<ValidationResult> beforePersist(TranslationLocaleEntity locale) {
        return Mono.empty();
    }

    /**
     * Validates before the specified {@link TranslationLocaleEntity original locale} will be updated with the
     * {@link TranslationLocaleDto DTO}.
     */
    default Mono<ValidationResult> beforeUpdate(TranslationLocaleEntity original, TranslationLocaleDto update) {
        return Mono.empty();
    }

    /**
     * Validates before the specified {@link TranslationLocaleEntity original locale} will be deleted.
     */
    default Mono<ValidationResult> beforeDelete(TranslationLocaleEntity locale) {
        return Mono.empty();
    }
}
