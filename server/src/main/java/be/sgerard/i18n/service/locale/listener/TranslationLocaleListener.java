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
     * Performs an action when the specified {@link TranslationLocaleEntity locale} has been created.
     */
    default Mono<Void> onCreatedLocale(TranslationLocaleEntity locale) {
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
