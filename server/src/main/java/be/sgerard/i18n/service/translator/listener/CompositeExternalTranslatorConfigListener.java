package be.sgerard.i18n.service.translator.listener;

import be.sgerard.i18n.model.translator.persistence.ExternalTranslatorConfigEntity;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Composite {@link ExternalTranslatorConfigListener translator config listener}.
 */
@Primary
@Component
public class CompositeExternalTranslatorConfigListener implements ExternalTranslatorConfigListener<ExternalTranslatorConfigEntity> {

    private final List<ExternalTranslatorConfigListener<ExternalTranslatorConfigEntity>> listeners;

    @Lazy
    @SuppressWarnings("unchecked")
    public CompositeExternalTranslatorConfigListener(List<ExternalTranslatorConfigListener<?>> listeners) {
        this.listeners = (List<ExternalTranslatorConfigListener<ExternalTranslatorConfigEntity>>) (List<?>) listeners;
    }

    @Override
    public boolean support(ExternalTranslatorConfigEntity config) {
        return true;
    }

    @Override
    public Mono<Void> afterPersist(ExternalTranslatorConfigEntity config) {
        return Flux
                .fromIterable(listeners)
                .filter(listener -> listener.support(config))
                .flatMap(listener -> listener.afterPersist(config))
                .then();
    }

    @Override
    public Mono<Void> afterUpdate(ExternalTranslatorConfigEntity config) {
        return Flux
                .fromIterable(listeners)
                .filter(listener -> listener.support(config))
                .flatMap(listener -> listener.afterUpdate(config))
                .then();
    }

    @Override
    public Mono<Void> afterDelete(ExternalTranslatorConfigEntity config) {
        return Flux
                .fromIterable(listeners)
                .filter(listener -> listener.support(config))
                .flatMap(listener -> listener.afterDelete(config))
                .then();
    }
}
