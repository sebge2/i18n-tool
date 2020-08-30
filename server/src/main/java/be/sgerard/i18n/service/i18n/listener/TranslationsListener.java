package be.sgerard.i18n.service.i18n.listener;

import be.sgerard.i18n.model.i18n.dto.TranslationUpdateDto;
import be.sgerard.i18n.model.i18n.persistence.BundleKeyEntity;
import be.sgerard.i18n.model.i18n.persistence.BundleKeyTranslationEntity;
import be.sgerard.i18n.model.validation.ValidationResult;
import reactor.core.publisher.Mono;

import java.util.Collection;

/**
 * Listener of the lifecycle of {@link BundleKeyTranslationEntity translations}.
 *
 * @author Sebastien Gerard
 */
public interface TranslationsListener {

    /**
     * Validates before updating the translation in the specified locale of the specified {@link BundleKeyEntity bundle key}.
     */
    default Mono<ValidationResult> beforeUpdate(TranslationUpdateDto translationUpdate) {
        return Mono.empty();
    }

    /**
     * Validates before updating translations based on the specified {@link TranslationUpdateDto update}.
     */
    default Mono<ValidationResult> beforeUpdate(Collection<TranslationUpdateDto> translationUpdates) {
        return Mono.empty();
    }

    /**
     * Performs an action when the specified {@link BundleKeyTranslationEntity translation} has been updated.
     */
    default Mono<Void> afterUpdate(BundleKeyEntity bundleKey, TranslationUpdateDto update) {
        return Mono.empty();
    }
}
