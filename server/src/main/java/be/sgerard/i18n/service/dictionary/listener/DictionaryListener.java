package be.sgerard.i18n.service.dictionary.listener;

import be.sgerard.i18n.model.dictionary.persistence.DictionaryEntryEntity;
import reactor.core.publisher.Mono;

/**
 * Listener of dictionary entries.
 *
 * @author Sebastien Gerard
 */
public interface DictionaryListener {

    /**
     * Performs an action after the specified dictionary entry has been persisted.
     */
    default Mono<Void> afterPersist(DictionaryEntryEntity entry) {
        return Mono.empty();
    }

    /**
     * Performs an action before persisting the update of the specified dictionary entry.
     */
    default Mono<Void> beforeUpdate(DictionaryEntryEntity entry) {
        return Mono.empty();
    }

    /**
     * Performs an action after the update of the specified dictionary entry.
     */
    default Mono<Void> afterUpdate(DictionaryEntryEntity entry) {
        return Mono.empty();
    }

    /**
     * Performs an action before the deletion of the specified dictionary entry.
     */
    default Mono<Void> beforeDelete(DictionaryEntryEntity entry) {
        return Mono.empty();
    }

    /**
     * Performs an action after the deletion of the specified dictionary entry.
     */
    default Mono<Void> afterDelete(DictionaryEntryEntity entry) {
        return Mono.empty();
    }

}
