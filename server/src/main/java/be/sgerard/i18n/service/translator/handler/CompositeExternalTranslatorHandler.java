package be.sgerard.i18n.service.translator.handler;

import be.sgerard.i18n.model.dictionary.ExternalSourceTranslationRequest;
import be.sgerard.i18n.model.translator.persistence.ExternalTranslatorConfigEntity;
import be.sgerard.i18n.service.translator.ExternalTranslatorConfigType;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Optional;

/**
 * Composite {@link ExternalTranslatorHandler external translator handler}.
 */
@Primary
@Component
public class CompositeExternalTranslatorHandler implements ExternalTranslatorHandler<ExternalTranslatorConfigEntity> {

    private final List<ExternalTranslatorHandler<ExternalTranslatorConfigEntity>> handlers;

    @SuppressWarnings("unchecked")
    public CompositeExternalTranslatorHandler(List<ExternalTranslatorHandler<?>> handlers) {
        this.handlers = (List<ExternalTranslatorHandler<ExternalTranslatorConfigEntity>>) (List<?>) handlers;
    }

    @Override
    public boolean support(ExternalTranslatorConfigType config) {
        return findHandler(config).isPresent();
    }

    @Override
    public Flux<String> translate(ExternalSourceTranslationRequest request, ExternalTranslatorConfigEntity config) {
        return findHandler(config.getType())
                .map(handler -> handler.translate(request, config))
                .orElseThrow(() -> new UnsupportedOperationException("Unsupported configuration [" + config + "]. Hint: check that all handlers are registered."));
    }

    private Optional<ExternalTranslatorHandler<ExternalTranslatorConfigEntity>> findHandler(ExternalTranslatorConfigType configType) {
        return handlers.stream()
                .filter(handler -> handler.support(configType))
                .findFirst();
    }
}
