package be.sgerard.i18n.service.dictionary.validation;

import be.sgerard.i18n.model.dictionary.dto.DictionaryEntryPatchDto;
import be.sgerard.i18n.model.dictionary.persistence.DictionaryEntryEntity;
import be.sgerard.i18n.model.validation.ValidationResult;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Composite {@link DictionaryValidator dictionary validator}.
 *
 * @author Sebastien Gerard
 */
@Component
@Primary
public class CompositeDictionaryValidator implements DictionaryValidator {

    private final List<DictionaryValidator> validators;

    @Lazy
    public CompositeDictionaryValidator(List<DictionaryValidator> validators) {
        this.validators = validators;
    }

    @Override
    public Mono<ValidationResult> beforePersist(DictionaryEntryEntity dictionaryEntry) {
        return Flux
                .fromIterable(validators)
                .flatMap(listener -> listener.beforePersist(dictionaryEntry))
                .reduce(ValidationResult::merge)
                .switchIfEmpty(Mono.just(ValidationResult.EMPTY));
    }

    @Override
    public Mono<ValidationResult> beforeUpdate(DictionaryEntryEntity original, DictionaryEntryPatchDto update) {
        return Flux
                .fromIterable(validators)
                .flatMap(listener -> listener.beforeUpdate(original, update))
                .reduce(ValidationResult::merge)
                .switchIfEmpty(Mono.just(ValidationResult.EMPTY));
    }

    @Override
    public Mono<ValidationResult> beforeDelete(DictionaryEntryEntity dictionaryEntry) {
        return Flux
                .fromIterable(validators)
                .flatMap(listener -> listener.beforeDelete(dictionaryEntry))
                .reduce(ValidationResult::merge)
                .switchIfEmpty(Mono.just(ValidationResult.EMPTY));
    }
}
