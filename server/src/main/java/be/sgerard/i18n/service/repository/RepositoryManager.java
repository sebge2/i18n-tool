package be.sgerard.i18n.service.repository;

import be.sgerard.i18n.model.repository.dto.RepositoryCreationDto;
import be.sgerard.i18n.model.repository.dto.RepositoryPatchDto;
import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.service.ResourceNotFoundException;
import be.sgerard.i18n.service.ValidationException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Manager of {@link RepositoryEntity repositories}.
 *
 * @author Sebastien Gerard
 */
public interface RepositoryManager {

    /**
     * Returns the {@link RepositoryEntity repository} having the specified id.
     */
    Mono<RepositoryEntity> findById(String id);

    /**
     * Returns the {@link RepositoryEntity repository} having the specified id.
     */
    default Mono<RepositoryEntity> findByIdOrDie(String id) throws ResourceNotFoundException {
        return findById(id)
                .switchIfEmpty(Mono.error(ResourceNotFoundException.repositoryNotFoundException(id)));
    }

    /**
     * Finds all the {@link RepositoryEntity repositories}.
     */
    Flux<RepositoryEntity> findAll();

    /**
     * Creates a new {@link RepositoryEntity repository} based on the {@link RepositoryCreationDto DTO}.
     */
    Mono<RepositoryEntity> create(RepositoryCreationDto creationDto) throws ValidationException;

    /**
     * Initializes the specified {@link RepositoryEntity repository}.
     */
    Mono<RepositoryEntity> initialize(String id) throws ResourceNotFoundException, RepositoryException;

    /**
     * Updates the repository as described by the specified {@link RepositoryPatchDto DTO}.
     */
    Mono<RepositoryEntity> update(RepositoryPatchDto patchDto) throws ResourceNotFoundException, RepositoryException;

    /**
     * Removes the {@link RepositoryEntity repository} having the specified id.
     */
    Mono<Void> delete(String id);

    /**
     * Consumes the content of the specified repository.
     */
    default void consumeRepository(String repositoryId, RepositoryApi.ApiConsumer apiConsumer) throws RepositoryException{
        applyOnRepository(repositoryId, (api) -> {
            apiConsumer.consume(api);
            return null;
        });
    }

    /**
     * Applies a function over the content of the specified repository.
     */
    <T> Mono<T> applyOnRepository(String repositoryId, RepositoryApi.ApiFunction<T> apiConsumer) throws RepositoryException;

}
