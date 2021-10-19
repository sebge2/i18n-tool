package be.sgerard.i18n.service.dictionary;

import be.sgerard.i18n.model.dictionary.DictionaryEntrySearchRequest;
import be.sgerard.i18n.model.dictionary.dto.DictionaryEntryCreationDto;
import be.sgerard.i18n.model.dictionary.dto.DictionaryEntryPatchDto;
import be.sgerard.i18n.model.dictionary.persistence.DictionaryEntryEntity;
import be.sgerard.i18n.repository.dictionary.DictionaryEntryRepository;
import be.sgerard.i18n.service.ResourceNotFoundException;
import be.sgerard.i18n.service.ValidationException;
import be.sgerard.i18n.service.dictionary.listener.DictionaryListener;
import be.sgerard.i18n.service.dictionary.validation.DictionaryValidator;
import be.sgerard.i18n.service.repository.RepositoryException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

/**
 * Implementation of the {@link DictionaryManager dictionary manager}.
 *
 * @author Sebastien Gerard
 */
@Service
public class DictionaryManagerImpl implements DictionaryManager {

    private final DictionaryEntryRepository repository;
    private final DictionaryValidator validator;
    private final DictionaryListener listener;

    public DictionaryManagerImpl(DictionaryEntryRepository repository,
                                 DictionaryValidator validator,
                                 DictionaryListener listener) {
        this.repository = repository;
        this.validator = validator;
        this.listener = listener;
    }

    @Override
    public Mono<DictionaryEntryEntity> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public Flux<DictionaryEntryEntity> find(DictionaryEntrySearchRequest request) {
        return repository.find(request);
    }

    @Override
    public Mono<DictionaryEntryEntity> create(DictionaryEntryCreationDto creationDto) throws ValidationException {
        return Mono
                .just(new DictionaryEntryEntity(creationDto.getTranslations()))
                .flatMap(this::create);
    }

    @Override
    public Mono<DictionaryEntryEntity> create(DictionaryEntryEntity entry) throws ValidationException {
        return validator.beforePersist(entry)
                .map(validationResult -> {
                    ValidationException.throwIfFailed(validationResult);

                    return entry;
                })
                .flatMap(repository::save)
                .flatMap(dictionaryEntry -> listener.afterPersist(dictionaryEntry).thenReturn(dictionaryEntry));
    }

    @Override
    public Mono<DictionaryEntryEntity> update(DictionaryEntryPatchDto patch) throws ResourceNotFoundException, RepositoryException {
        return findByIdOrDie(patch.getId())
                .flatMap(entry -> validator.beforeUpdate(entry, patch)
                        .map(validationResult -> {
                            ValidationException.throwIfFailed(validationResult);

                            return entry;
                        })
                )
                .doOnNext(entry ->
                        patch.getTranslations()
                                .forEach((key, value) -> entry.getTranslations().put(key, value))
                )
                .flatMap(this::update);
    }

    @Override
    public Mono<DictionaryEntryEntity> update(DictionaryEntryEntity entry) throws ValidationException {
        return listener.beforeUpdate(entry).thenReturn(entry)
                .flatMap(repository::save)
                .flatMap(dictionaryEntry -> listener.afterUpdate(dictionaryEntry).thenReturn(dictionaryEntry));
    }

    @Override
    public Flux<DictionaryEntryEntity> update(Collection<DictionaryEntryPatchDto> patch) throws ResourceNotFoundException, ValidationException {
        return Flux.fromIterable(patch)
                .flatMap(this::update);
    }

    @Override
    public Mono<DictionaryEntryEntity> delete(String id) {
        return findById(id)
                .flatMap(entry -> validator.beforeDelete(entry)
                        .map(validationResult -> {
                            ValidationException.throwIfFailed(validationResult);

                            return entry;
                        })
                )
                .flatMap(dictionaryEntry -> listener.beforeDelete(dictionaryEntry).thenReturn(dictionaryEntry))
                .flatMap(entry -> repository.delete(entry).thenReturn(entry))
                .flatMap(dictionaryEntry -> listener.afterDelete(dictionaryEntry).thenReturn(dictionaryEntry));
    }

    @Override
    public Mono<Void> deleteAll() {
        return repository
                .findAll() // NICE improve this
                .flatMap(entry -> delete(entry.getId()))
                .then();
    }
}
