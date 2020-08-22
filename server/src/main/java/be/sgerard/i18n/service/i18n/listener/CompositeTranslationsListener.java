package be.sgerard.i18n.service.i18n.listener;

import be.sgerard.i18n.model.i18n.persistence.BundleKeyEntity;
import be.sgerard.i18n.model.i18n.persistence.BundleKeyTranslationEntity;
import be.sgerard.i18n.model.validation.ValidationResult;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Composite {@link TranslationsListener translations listener}.
 *
 * @author Sebastien Gerard
 */
@Component
@Primary
public class CompositeTranslationsListener implements TranslationsListener {

    private final List<TranslationsListener> listeners;

    @Lazy
    public CompositeTranslationsListener(List<TranslationsListener> listeners) {
        this.listeners = listeners;
    }

    @Override
    public Mono<ValidationResult> beforeUpdate(BundleKeyEntity bundleKey, String localeId, String updatedValue) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.beforeUpdate(bundleKey, localeId, updatedValue))
                .reduce(ValidationResult::merge)
                .switchIfEmpty(Mono.just(ValidationResult.EMPTY));
    }

    @Override
    public Mono<Void> afterUpdate(BundleKeyEntity bundleKey, BundleKeyTranslationEntity translation) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.afterUpdate(bundleKey, translation))
                .then();
    }
}
