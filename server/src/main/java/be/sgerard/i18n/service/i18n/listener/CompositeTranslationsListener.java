package be.sgerard.i18n.service.i18n.listener;

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
    public Mono<ValidationResult> beforeUpdate(BundleKeyTranslationEntity translation, String updatedValue) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.beforeUpdate(translation, updatedValue))
                .reduce(ValidationResult::merge)
                .switchIfEmpty(Mono.just(ValidationResult.EMPTY));
    }

    @Override
    public Mono<Void> afterUpdate(BundleKeyTranslationEntity translation) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.afterUpdate(translation))
                .then();
    }
}
