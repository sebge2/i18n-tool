package be.sgerard.i18n.service.translator.template;

import be.sgerard.i18n.model.translator.persistence.ExternalTranslatorConfigEntity;

/**
 * Provider of {@link ExternalTranslatorConfigEntity configuration} for external translator.
 */
public interface ExternalTranslatorConfigProvider<R> {

    /**
     * Returns whether the specified request is supported by this provider.
     */
    boolean support(Object request);

    /**
     * Creates the {@link ExternalTranslatorConfigEntity configuration} to use to call the external translator.
     */
    ExternalTranslatorConfigEntity createConfig(R request);
}
