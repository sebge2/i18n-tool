package be.sgerard.i18n.service.i18n.listener;

import be.sgerard.i18n.model.i18n.dto.translation.key.TranslationUpdateDto;
import be.sgerard.i18n.model.i18n.persistence.BundleKeyEntity;
import be.sgerard.i18n.model.i18n.persistence.BundleKeyTranslationEntity;
import be.sgerard.i18n.model.validation.ValidationResult;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
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
    public Mono<ValidationResult> beforeUpdate(Collection<TranslationUpdateDto> translationUpdates) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.beforeUpdate(translationUpdates))
                .reduce(ValidationResult::merge)
                .switchIfEmpty(Mono.just(ValidationResult.EMPTY));
    }

    @Override
    public Mono<Void> afterUpdate(List<Pair<BundleKeyTranslationEntity, BundleKeyEntity>> updates) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.afterUpdate(updates))
                .then();
    }
}
