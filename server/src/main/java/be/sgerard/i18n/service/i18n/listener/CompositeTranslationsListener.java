package be.sgerard.i18n.service.i18n.listener;

import be.sgerard.i18n.model.i18n.persistence.BundleKeyTranslationEntity;
import be.sgerard.i18n.model.validation.ValidationResult;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Map;

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
    public Mono<ValidationResult> beforeUpdate(Map<String, String> translations) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.beforeUpdate(translations))
                .reduce(ValidationResult::merge);
    }

    @Override
    public Mono<Void> afterUpdate(Collection<BundleKeyTranslationEntity> translations) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.afterUpdate(translations))
                .then();
    }
}
