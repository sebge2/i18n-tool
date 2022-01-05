package be.sgerard.i18n.service.translator.validation;

import be.sgerard.i18n.model.translator.persistence.ExternalTranslatorConfigEntity;
import be.sgerard.i18n.model.validation.ValidationResult;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static be.sgerard.i18n.model.validation.ValidationUtils.assertIsNotEmpty;

/**
 * {@link ExternalTranslatorConfigValidator Validator} checking {@link ExternalTranslatorConfigEntity external translator configurations}.
 *
 * @author Sebastien Gerard
 */
@Component
public class DefaultExternalTranslatorConfigValidator implements ExternalTranslatorConfigValidator<ExternalTranslatorConfigEntity> {

    @Override
    public boolean support(ExternalTranslatorConfigEntity config) {
        return true;
    }

    @Override
    public Mono<ValidationResult> beforePersistOrUpdate(ExternalTranslatorConfigEntity config) {
        return Mono.just(validate(config));
    }

    /**
     * Validates mandatory fields.
     */
    private ValidationResult validate(ExternalTranslatorConfigEntity config) {
        return ValidationResult.builder()
                .merge(
                        assertIsNotEmpty(config.getLabel(), "validation.external-translator-config.missing-label"),
                        assertIsNotEmpty(config.getLinkUrl(), "validation.external-translator-config.missing-link-url")
                )
                .build();
    }
}
