package be.sgerard.i18n.service.i18n.listener;

import be.sgerard.i18n.model.i18n.persistence.BundleKeyTranslationEntity;
import be.sgerard.i18n.model.validation.ValidationResult;
import reactor.core.publisher.Mono;

/**
 * Listener of the lifecycle of {@link BundleKeyTranslationEntity translations}.
 *
 * @author Sebastien Gerard
 */
public interface TranslationsListener {

    /**
     * Validates before updating the specified {@link BundleKeyTranslationEntity translation}.
     */
    default Mono<ValidationResult> beforeUpdate(BundleKeyTranslationEntity translation, String updatedValue) {
        return Mono.empty();
    }

    /**
     * Performs an action when the specified {@link BundleKeyTranslationEntity translations} has been updated.
     */
    default Mono<Void> afterUpdate(BundleKeyTranslationEntity translation) {
        return Mono.empty();
    }
}
