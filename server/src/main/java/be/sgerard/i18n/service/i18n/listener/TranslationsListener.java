package be.sgerard.i18n.service.i18n.listener;

import be.sgerard.i18n.model.i18n.persistence.BundleKeyTranslationEntity;
import be.sgerard.i18n.model.validation.ValidationResult;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Map;

/**
 * Listener of the lifecycle of {@link BundleKeyTranslationEntity translations}.
 *
 * @author Sebastien Gerard
 */
public interface TranslationsListener {

    /**
     * Validates before updating the specified translations. The map associates the
     * {@link be.sgerard.i18n.model.i18n.persistence.BundleKeyTranslationEntity#getId() translation id} to the actual translation value.
     */
    default Mono<ValidationResult> beforeUpdate(Map<String, String> translations) {
        return Mono.empty();
    }

    /**
     * Performs an action when the specified {@link BundleKeyTranslationEntity translations} has been updated.
     */
    default Mono<Void> afterUpdate(Collection<BundleKeyTranslationEntity> translations) {
        return Mono.empty();
    }
}
