package be.sgerard.i18n.service.locale.listener;

import be.sgerard.i18n.model.locale.persistence.TranslationLocaleEntity;
import reactor.core.publisher.Mono;

/**
 * Listener of the lifecycle of {@link TranslationLocaleEntity translation locales}.
 *
 * @author Sebastien Gerard
 */
public interface TranslationLocaleListener {

    /**
     * Performs an action when the specified {@link TranslationLocaleEntity locale} has been persisted.
     */
    default Mono<Void> afterPersist(TranslationLocaleEntity locale) {
        return Mono.empty();
    }

    /**
     * Performs an action when the specified {@link TranslationLocaleEntity locale} is about to be updated.
     */
    default Mono<Void> beforeUpdate(TranslationLocaleEntity locale) {
        return Mono.empty();
    }

    /**
     * Performs an action when the specified {@link TranslationLocaleEntity locale} has been updated.
     */
    default Mono<Void> afterUpdate(TranslationLocaleEntity locale) {
        return Mono.empty();
    }

    /**
     * Performs an action when the specified {@link TranslationLocaleEntity locale} is about to be deleted.
     */
    default Mono<Void> beforeDelete(TranslationLocaleEntity locale) {
        return Mono.empty();
    }

    /**
     * Performs an action when the specified {@link TranslationLocaleEntity locale} has been deleted.
     */
    default Mono<Void> afterDelete(TranslationLocaleEntity locale) {
        return Mono.empty();
    }

}
