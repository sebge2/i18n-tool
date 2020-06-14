package be.sgerard.i18n.service.i18n.listener;

import be.sgerard.i18n.model.i18n.dto.TranslationLocaleDto;
import be.sgerard.i18n.model.i18n.persistence.TranslationLocaleEntity;
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

    private List<TranslationLocaleListener> listeners;

    @Lazy
    public CompositeTranslationLocaleListener(List<TranslationLocaleListener> listeners) {
        this.listeners = listeners;
    }

    @Override
    public Mono<Void> beforePersist(TranslationLocaleEntity locale) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.beforePersist(locale))
                .then();
    }

    @Override
    public Mono<Void> beforeUpdate(TranslationLocaleEntity original, TranslationLocaleDto locale) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.beforeUpdate(original, locale))
                .then();
    }

    @Override
    public Mono<Void> onCreatedLocale(TranslationLocaleEntity locale) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.onCreatedLocale(locale))
                .then();
    }

    @Override
    public Mono<Void> onUpdatedLocale(TranslationLocaleEntity locale) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.onUpdatedLocale(locale))
                .then();
    }

    @Override
    public Mono<Void> onDeletedLocale(TranslationLocaleEntity locale) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.onDeletedLocale(locale))
                .then();
    }
}
