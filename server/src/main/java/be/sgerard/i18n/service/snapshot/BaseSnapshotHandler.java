package be.sgerard.i18n.service.snapshot;

import be.sgerard.i18n.model.validation.ValidationMessage;
import be.sgerard.i18n.model.validation.ValidationResult;
import be.sgerard.i18n.service.ValidationException;
import be.sgerard.i18n.support.ReactiveUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;

/**
 * Base implementation of a {@link SnapshotHandler snapshot handler}.
 *
 * @author Sebastien Gerard
 */
public abstract class BaseSnapshotHandler<E, D> implements SnapshotHandler {

    /**
     * Validation message specifying that the DTO cannot be parsed.
     */
    public static final String ERROR_PARSING = "validation.snapshot.error-parsing";

    private final String inputFileName;
    private final Class<D> dtoType;
    private final ObjectMapper objectMapper;
    private final ReactiveCrudRepository<E, String> repository;

    protected BaseSnapshotHandler(String inputFileName,
                                  Class<D> dtoType,
                                  ObjectMapper objectMapper,
                                  ReactiveCrudRepository<E, String> repository) {
        this.inputFileName = inputFileName;
        this.dtoType = dtoType;
        this.objectMapper = objectMapper;
        this.repository = repository;
    }

    @Override
    public Mono<ValidationResult> validate(File importLocation) {
        final File snapshotFile = new File(importLocation, inputFileName);

        if (!snapshotFile.exists()) {
            return Mono.just(ValidationResult.EMPTY);
        }

        return Flux
                .merge(
                        validateOneByOne(snapshotFile),
                        validateAll(snapshotFile)
                )
                .reduce(ValidationResult::merge)
                .switchIfEmpty(Mono.just(ValidationResult.EMPTY));

    }

    @Override
    public Mono<Void> clearAll() {
        return repository
                .findAll()
                .flatMap(this::beforeDelete)
                .flatMap(repository::delete)
                .then();
    }

    @Override
    public Mono<Void> restoreAll(File importLocation) {
        final File snapshotFile = new File(importLocation, inputFileName);

        if (!snapshotFile.exists()) {
            return Mono.empty();
        }

        return ReactiveUtils
                .streamObjectFromJsonFile(
                        this::mapFromDto,
                        dtoType,
                        objectMapper,
                        snapshotFile
                )
                .flatMap(this::beforeSave)
                .flatMap(this::save)
                .then();
    }

    @Override
    public Mono<Void> exportAll(File exportLocation) {
        return ReactiveUtils
                .streamObjectToJsonFile(
                        this::findAll,
                        this::mapToDto,
                        objectMapper,
                        new File(exportLocation, inputFileName)
                )
                .then();
    }

    /**
     * Validates the state of the specified DTO.
     */
    protected abstract Mono<ValidationResult> validate(E entity);

    /**
     * Maps the entity from its DTO representation.
     */
    protected abstract Mono<E> mapFromDto(D dto);

    /**
     * Maps the entity to its DTO representation.
     */
    protected abstract Mono<D> mapToDto(E entity);

    /**
     * Validates the state of DTO in the specified file, one-by-one.
     */
    protected Flux<ValidationResult> validateOneByOne(File snapshotFile) {
        return loadAll(snapshotFile).flatMap(this::validate);
    }

    /**
     * Validates that all the specified entities are valid.
     */
    protected Flux<ValidationResult> validateAll(File snapshotFile) {
        return Flux.empty();
    }

    /**
     * Loads all DTO from the snapshot file.
     */
    protected Flux<E> loadAll(File snapshotFile) {
        return ReactiveUtils
                .streamObjectFromJsonFile(
                        this::mapFromDto,
                        dtoType,
                        objectMapper,
                        snapshotFile
                )
                .onErrorMap(throwable -> {
                    if (throwable instanceof ValidationException) {
                        return throwable;
                    }

                    return new ValidationException(
                            ValidationResult.singleMessage(new ValidationMessage(ERROR_PARSING, inputFileName))
                    );
                });
    }

    /**
     * Saves the specified entity.
     */
    protected Mono<E> save(E entity) {
        return repository.save(entity);
    }

    /**
     * Finds all entities.
     */
    protected Flux<E> findAll() {
        return repository.findAll();
    }

    /**
     * Performs an action before saving.
     */
    protected Mono<E> beforeSave(E entity) {
        return Mono.just(entity);
    }

    /**
     * Performs an action before deleting.
     */
    protected Mono<E> beforeDelete(E entity) {
        return Mono.just(entity);
    }
}
