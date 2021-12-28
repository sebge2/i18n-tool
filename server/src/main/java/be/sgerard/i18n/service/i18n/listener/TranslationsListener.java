package be.sgerard.i18n.service.i18n.listener;

import be.sgerard.i18n.model.i18n.dto.translation.key.TranslationUpdateDto;
import be.sgerard.i18n.model.i18n.persistence.BundleKeyEntity;
import be.sgerard.i18n.model.i18n.persistence.BundleKeyTranslationEntity;
import be.sgerard.i18n.model.validation.ValidationResult;
import org.apache.commons.lang3.tuple.Pair;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;

/**
 * Listener of the lifecycle of {@link BundleKeyTranslationEntity translations}.
 *
 * @author Sebastien Gerard
 */
public interface TranslationsListener {

    /**
     * Validates before updating translations based on the specified {@link TranslationUpdateDto update}.
     */
    default Mono<ValidationResult> beforeUpdate(Collection<TranslationUpdateDto> translationUpdates) {
        return Mono.empty();
    }

    /**
     * Performs an action when the specified {@link BundleKeyTranslationEntity translation} have been updated.
     */
    default Mono<Void> afterUpdate(List<Pair<BundleKeyTranslationEntity, BundleKeyEntity>> updates) {
        return Mono.empty();
    }
}
