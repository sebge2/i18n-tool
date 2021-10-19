package be.sgerard.i18n.service.dictionary.validation;

import be.sgerard.i18n.model.dictionary.dto.DictionaryEntryPatchDto;
import be.sgerard.i18n.model.dictionary.persistence.DictionaryEntryEntity;
import be.sgerard.i18n.model.validation.ValidationResult;
import reactor.core.publisher.Mono;

/**
 * Validator of {@link DictionaryEntryEntity dictionary entry}.
 *
 * @author Sebastien Gerard
 */
public interface DictionaryValidator {

    /**
     * Validates before persisting the specified {@link DictionaryEntryEntity dictionary entry}.
     */
    default Mono<ValidationResult> beforePersist(DictionaryEntryEntity dictionaryEntry) {
        return Mono.empty();
    }

    /**
     * Validates before the specified {@link DictionaryEntryEntity original dictionary entry} will be updated with the
     * {@link DictionaryEntryPatchDto patch}.
     */
    default Mono<ValidationResult> beforeUpdate(DictionaryEntryEntity original, DictionaryEntryPatchDto update) {
        return Mono.empty();
    }

    /**
     * Validates before the specified {@link DictionaryEntryEntity original dictionary entry} will be deleted.
     */
    default Mono<ValidationResult> beforeDelete(DictionaryEntryEntity dictionaryEntry) {
        return Mono.empty();
    }
}
