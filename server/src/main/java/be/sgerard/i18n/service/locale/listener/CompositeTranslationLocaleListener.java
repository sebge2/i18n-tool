package be.sgerard.i18n.service.locale.listener;

import be.sgerard.i18n.model.locale.persistence.TranslationLocaleEntity;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Composite {@link TranslationLocaleListener translation locale listener}.
 *
 * @author Sebastien Gerard
 */
@Component
@Primary
public class CompositeTranslationLocaleListener implements TranslationLocaleListener {

    private final List<TranslationLocaleListener> listeners;

    @Lazy
    public CompositeTranslationLocaleListener(List<TranslationLocaleListener> listeners) {
        this.listeners = listeners;
    }

    @Override
    public Mono<Void> afterPersist(TranslationLocaleEntity locale) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.afterPersist(locale))
                .then();
    }

    @Override
    public Mono<Void> beforeUpdate(TranslationLocaleEntity locale) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.beforeUpdate(locale))
                .then();
    }

    @Override
    public Mono<Void> afterUpdate(TranslationLocaleEntity locale) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.afterUpdate(locale))
                .then();
    }

    @Override
    public Mono<Void> beforeDelete(TranslationLocaleEntity locale) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.beforeDelete(locale))
                .then();
    }

    @Override
    public Mono<Void> afterDelete(TranslationLocaleEntity locale) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.afterDelete(locale))
                .then();
    }
}
