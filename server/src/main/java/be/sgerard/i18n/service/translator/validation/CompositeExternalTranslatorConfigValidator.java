package be.sgerard.i18n.service.translator.validation;

import be.sgerard.i18n.model.translator.persistence.ExternalTranslatorConfigEntity;
import be.sgerard.i18n.model.validation.ValidationResult;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Composite {@link ExternalTranslatorConfigValidator config validator}.
 */
@Primary
@Component
public class CompositeExternalTranslatorConfigValidator implements ExternalTranslatorConfigValidator<ExternalTranslatorConfigEntity> {

    private final List<ExternalTranslatorConfigValidator<ExternalTranslatorConfigEntity>> validators;

    @Lazy
    @SuppressWarnings("unchecked")
    public CompositeExternalTranslatorConfigValidator(List<ExternalTranslatorConfigValidator<?>> validators) {
        this.validators = (List<ExternalTranslatorConfigValidator<ExternalTranslatorConfigEntity>>) (List<?>) validators;
    }

    @Override
    public boolean support(ExternalTranslatorConfigEntity config) {
        return true;
    }

    @Override
    public Mono<ValidationResult> beforePersistOrUpdate(ExternalTranslatorConfigEntity config) {
        return Flux
                .fromIterable(validators)
                .filter(validator -> validator.support(config))
                .flatMap(listener -> listener.beforePersistOrUpdate(config))
                .reduce(ValidationResult::merge)
                .switchIfEmpty(Mono.just(ValidationResult.EMPTY));
    }
}
