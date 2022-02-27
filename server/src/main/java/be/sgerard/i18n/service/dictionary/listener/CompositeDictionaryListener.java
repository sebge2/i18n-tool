package be.sgerard.i18n.service.dictionary.listener;

import be.sgerard.i18n.model.dictionary.persistence.DictionaryEntryEntity;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Composite {@link DictionaryListener dictionary listener}.
 *
 * @author Sebastien Gerard
 */
@Component
@Primary
public class CompositeDictionaryListener implements DictionaryListener {

    private final List<DictionaryListener> listeners;

    @Lazy
    public CompositeDictionaryListener(List<DictionaryListener> listeners) {
        this.listeners = listeners;
    }

    @Override
    public Mono<Void> afterPersist(DictionaryEntryEntity entry) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.afterPersist(entry))
                .then();
    }

    @Override
    public Mono<Void> beforeUpdate(DictionaryEntryEntity entry) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.beforeUpdate(entry))
                .then();
    }

    @Override
    public Mono<Void> afterUpdate(DictionaryEntryEntity entry) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.afterUpdate(entry))
                .then();
    }

    @Override
    public Mono<Void> beforeDelete(DictionaryEntryEntity entry) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.beforeDelete(entry))
                .then();
    }

    @Override
    public Mono<Void> afterDelete(DictionaryEntryEntity entry) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.afterDelete(entry))
                .then();
    }
}
