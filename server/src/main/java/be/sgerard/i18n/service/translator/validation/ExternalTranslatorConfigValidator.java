package be.sgerard.i18n.service.translator.validation;

import be.sgerard.i18n.model.translator.persistence.ExternalTranslatorConfigEntity;
import be.sgerard.i18n.model.validation.ValidationResult;
import reactor.core.publisher.Mono;

/**
 * Validator of the lifecycle of {@link ExternalTranslatorConfigEntity translator configs}.
 */
public interface ExternalTranslatorConfigValidator<C extends ExternalTranslatorConfigEntity> {

    /**
     * Checks that the specified config is supported.
     */
    boolean support(ExternalTranslatorConfigEntity config);

    /**
     * Validates before persisting the specified repository.
     */
    default Mono<ValidationResult> beforePersistOrUpdate(C config) {
        return Mono.just(ValidationResult.EMPTY);
    }
}
