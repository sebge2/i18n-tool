package be.sgerard.i18n.service.translator.validation;

import be.sgerard.i18n.model.translator.persistence.ExternalTranslatorConfigEntity;
import be.sgerard.i18n.model.translator.persistence.ExternalTranslatorGenericRestConfigEntity;
import be.sgerard.i18n.model.validation.ValidationResult;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static be.sgerard.i18n.model.validation.ValidationUtils.assertIsNotEmpty;

/**
 * {@link ExternalTranslatorConfigValidator Validator} checking {@link ExternalTranslatorGenericRestConfigEntity external translator configurations}.
 *
 * @author Sebastien Gerard
 */
@Component
public class GenericRestExternalTranslatorConfigValidator implements ExternalTranslatorConfigValidator<ExternalTranslatorGenericRestConfigEntity> {

    @Override
    public boolean support(ExternalTranslatorConfigEntity config) {
        return config instanceof ExternalTranslatorGenericRestConfigEntity;
    }

    @Override
    public Mono<ValidationResult> beforePersistOrUpdate(ExternalTranslatorGenericRestConfigEntity config) {
        return Mono.just(validate(config));
    }

    /**
     * Validates mandatory fields.
     */
    private ValidationResult validate(ExternalTranslatorGenericRestConfigEntity config) {
        return ValidationResult.builder()
                .merge(
                        assertIsNotEmpty(config.getMethod(), "validation.external-translator-config.missing-method"),
                        assertIsNotEmpty(config.getUrl(), "validation.external-translator-config.missing-url"),
                        assertIsNotEmpty(config.getQueryExtractor(), "validation.external-translator-config.missing-query-extractor")
                )
                .build();
    }
}
