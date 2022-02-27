package be.sgerard.i18n.service.translator.template;

import be.sgerard.i18n.model.translator.persistence.ExternalTranslatorConfigEntity;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Composite {@link ExternalTranslatorConfigProvider configuration provider}.
 */
@Primary
@Component
public class CompositeExternalTranslatorConfigProvider implements ExternalTranslatorConfigProvider<Object> {

    private final List<ExternalTranslatorConfigProvider<Object>> providers;

    @SuppressWarnings("unchecked")
    public CompositeExternalTranslatorConfigProvider(List<ExternalTranslatorConfigProvider<?>> providers) {
        this.providers = (List<ExternalTranslatorConfigProvider<Object>>) (List<?>) providers;
    }

    @Override
    public boolean support(Object request) {
        return findProvider(request).isPresent();
    }

    @Override
    public ExternalTranslatorConfigEntity createConfig(Object request) {
        return findProvider(request)
                .map(converter -> converter.createConfig(request))
                .orElseThrow(() -> new UnsupportedOperationException("Unsupported config request [" + request + "]. Hint: check that all providers are registered."));
    }

    /**
     * Returns the provider supporting the specified request.
     */
    private Optional<ExternalTranslatorConfigProvider<Object>> findProvider(Object request) {
        return providers.stream()
                .filter(provider -> provider.support(request))
                .findFirst();
    }
}
