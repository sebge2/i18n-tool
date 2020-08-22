package be.sgerard.i18n.service.i18n.listener;

import be.sgerard.i18n.model.i18n.persistence.BundleKeyEntity;
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
     * Validates before updating the translation in the specified locale of the specified {@link BundleKeyEntity bundle key}.
     */
    default Mono<ValidationResult> beforeUpdate(BundleKeyEntity bundleKey, String localeId, String updatedValue) {
        return Mono.empty();
    }

    /**
     * Performs an action when the specified {@link BundleKeyTranslationEntity translation} has been updated.
     */
    default Mono<Void> afterUpdate(BundleKeyEntity bundleKey, BundleKeyTranslationEntity translation) {
        return Mono.empty();
    }
}
