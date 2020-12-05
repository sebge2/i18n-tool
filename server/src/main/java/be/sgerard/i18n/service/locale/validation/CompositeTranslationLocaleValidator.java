package be.sgerard.i18n.service.locale.validation;

import be.sgerard.i18n.model.i18n.dto.TranslationLocaleDto;
import be.sgerard.i18n.model.locale.persistence.TranslationLocaleEntity;
import be.sgerard.i18n.model.validation.ValidationResult;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Composite {@link TranslationLocaleValidator translation locale validator}.
 *
 * @author Sebastien Gerard
 */
@Component
@Primary
public class CompositeTranslationLocaleValidator implements TranslationLocaleValidator {

    private final List<TranslationLocaleValidator> validators;

    @Lazy
    public CompositeTranslationLocaleValidator(List<TranslationLocaleValidator> validators) {
        this.validators = validators;
    }

    @Override
    public Mono<ValidationResult> beforePersist(TranslationLocaleEntity locale) {
        return Flux
                .fromIterable(validators)
                .flatMap(listener -> listener.beforePersist(locale))
                .reduce(ValidationResult::merge)
                .switchIfEmpty(Mono.just(ValidationResult.EMPTY));
    }

    @Override
    public Mono<ValidationResult> beforeUpdate(TranslationLocaleEntity original, TranslationLocaleDto update) {
        return Flux
                .fromIterable(validators)
                .flatMap(listener -> listener.beforeUpdate(original, update))
                .reduce(ValidationResult::merge)
                .switchIfEmpty(Mono.just(ValidationResult.EMPTY));
    }

    @Override
    public Mono<ValidationResult> beforeDelete(TranslationLocaleEntity locale) {
        return Flux
                .fromIterable(validators)
                .flatMap(listener -> listener.beforeDelete(locale))
                .reduce(ValidationResult::merge)
                .switchIfEmpty(Mono.just(ValidationResult.EMPTY));
    }
}
