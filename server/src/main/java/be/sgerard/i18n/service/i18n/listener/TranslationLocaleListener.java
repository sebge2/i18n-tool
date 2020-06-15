package be.sgerard.i18n.service.i18n.listener;

import be.sgerard.i18n.model.i18n.dto.TranslationLocaleDto;
import be.sgerard.i18n.model.i18n.persistence.TranslationLocaleEntity;
import be.sgerard.i18n.model.validation.ValidationResult;
import reactor.core.publisher.Mono;

/**
 * Listener of the lifecycle of {@link TranslationLocaleEntity translation locales}.
 *
 * @author Sebastien Gerard
 */
public interface TranslationLocaleListener {

    /**
     * Validates before persisting the specified {@link TranslationLocaleEntity locale}.
     */
    default Mono<ValidationResult> beforePersist(TranslationLocaleEntity locale) {
        return Mono.empty();
    }

    /**
     * Performs an action when the specified {@link TranslationLocaleEntity locale} has been created.
     */
    default Mono<Void> onCreatedLocale(TranslationLocaleEntity locale) {
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
     * Performs an action when the specified {@link TranslationLocaleEntity locale} has been updated.
     */
    default Mono<Void> onUpdatedLocale(TranslationLocaleEntity locale) {
        return Mono.empty();
    }

    /**
     * Performs an action when the specified {@link TranslationLocaleEntity locale} has been deleted.
     */
    default Mono<Void> onDeletedLocale(TranslationLocaleEntity locale) {
        return Mono.empty();
    }

}
