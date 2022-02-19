package be.sgerard.i18n.service.translator.validation;

import be.sgerard.i18n.model.dictionary.ExternalSourceTranslationRequest;
import be.sgerard.i18n.model.translator.persistence.ExternalTranslatorConfigEntity;
import be.sgerard.i18n.model.validation.ValidationMessage;
import be.sgerard.i18n.model.validation.ValidationResult;
import be.sgerard.i18n.service.translator.handler.ExternalTranslatorHandler;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * {@link ExternalTranslatorConfigValidator Validator} checking that the configuration allows connection and translation.
 */
@Component
@AllArgsConstructor
public class ExternalTranslatorConfigConnectionValidator implements ExternalTranslatorConfigValidator<ExternalTranslatorConfigEntity> {

    private final ExternalTranslatorHandler<ExternalTranslatorConfigEntity> handler;

    @Override
    public boolean support(ExternalTranslatorConfigEntity config) {
        return true;
    }

    @Override
    public Mono<ValidationResult> beforePersistOrUpdate(ExternalTranslatorConfigEntity config) {
        final ExternalSourceTranslationRequest request = new ExternalSourceTranslationRequest(
                "en",
                "fr",
                "test"
        );

        return handler
                .translate(request, config)
                .take(1)
                .next()
                .map(translation -> ValidationResult.EMPTY)
                .onErrorResume(error ->
                        Mono.fromSupplier(() ->
                                ValidationResult.singleMessage(new ValidationMessage(
                                        "validation.external-translator.error-translation",
                                        error.getMessage(),
                                        request.getFromLocale(),
                                        request.getTargetLocale()
                                ))
                        )
                );
    }
}
