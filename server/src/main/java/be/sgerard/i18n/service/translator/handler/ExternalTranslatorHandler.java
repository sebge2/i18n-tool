package be.sgerard.i18n.service.translator.handler;

import be.sgerard.i18n.model.dictionary.ExternalSourceTranslationRequest;
import be.sgerard.i18n.model.translator.persistence.ExternalTranslatorConfigEntity;
import be.sgerard.i18n.service.translator.ExternalTranslatorConfigType;
import reactor.core.publisher.Flux;

/**
 * Service translating text using a particular external source.
 */
public interface ExternalTranslatorHandler<E extends ExternalTranslatorConfigEntity>  {

    /**
     * Returns whether this translator support the specified config.
     */
    boolean support(ExternalTranslatorConfigType configType);

    /**
     * Translates the text as specified by the {@link ExternalSourceTranslationRequest request}.
     */
    Flux<String> translate(ExternalSourceTranslationRequest request, E config);
}
