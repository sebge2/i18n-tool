package be.sgerard.i18n.service.dictionary;

import be.sgerard.i18n.model.dictionary.DictionaryEntrySearchRequest;
import be.sgerard.i18n.model.dictionary.dto.DictionaryEntryCreationDto;
import be.sgerard.i18n.model.dictionary.dto.DictionaryEntryPatchDto;
import be.sgerard.i18n.model.dictionary.persistence.DictionaryEntryEntity;
import be.sgerard.i18n.service.ResourceNotFoundException;
import be.sgerard.i18n.service.ValidationException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

/**
 * Manager of the application dictionary.
 *
 * @author Sebastien Gerard
 */
public interface DictionaryManager {

    /**
     * Returns the {@link DictionaryEntryEntity entry} having the specified id.
     */
    Mono<DictionaryEntryEntity> findById(String id);

    /**
     * Returns the {@link DictionaryEntryEntity entry} having the specified id.
     */
    default Mono<DictionaryEntryEntity> findByIdOrDie(String id) throws ResourceNotFoundException {
        return findById(id)
                .switchIfEmpty(Mono.error(ResourceNotFoundException.dictionaryEntryNotFoundException(id)));
    }

    /**
     * Finds {@link DictionaryEntryEntity dictionary entries} satisfying the specified {@link DictionaryEntrySearchRequest request}.
     */
    Flux<DictionaryEntryEntity> find(DictionaryEntrySearchRequest request);

    /**
     * Finds all {@link DictionaryEntryEntity dictionary entries}.
     */
    default Flux<DictionaryEntryEntity> findAll() {
        return find(DictionaryEntrySearchRequest.builder().build());
    }

    /**
     * Creates a new {@link DictionaryEntryEntity dictionary entry} based on the {@link DictionaryEntryCreationDto DTO}.
     */
    Mono<DictionaryEntryEntity> create(DictionaryEntryCreationDto creationDto) throws ValidationException;

    /**
     * Creates a new {@link DictionaryEntryEntity dictionary entry}.
     */
    Mono<DictionaryEntryEntity> create(DictionaryEntryEntity entry) throws ValidationException;

    /**
     * Updates the entry as described by the specified {@link DictionaryEntryPatchDto DTO}.
     */
    Mono<DictionaryEntryEntity> update(DictionaryEntryPatchDto patch) throws ResourceNotFoundException, ValidationException;

    /**
     * Updates the specified {@link DictionaryEntryEntity entry}.
     */
    Mono<DictionaryEntryEntity> update(DictionaryEntryEntity entry) throws ValidationException;

    /**
     * Updates entries as described by specified {@link DictionaryEntryPatchDto patches}.
     */
    Flux<DictionaryEntryEntity> update(Collection<DictionaryEntryPatchDto> patch) throws ResourceNotFoundException, ValidationException;

    /**
     * Removes the {@link DictionaryEntryEntity dictionary entry} having the specified id.
     */
    Mono<DictionaryEntryEntity> delete(String id);

    /**
     * Deletes all dictionary entries.
     */
    Mono<Void> deleteAll();
}
