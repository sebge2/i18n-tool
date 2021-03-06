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
     * Returns the {@link RepositoryEntity repository} having the specified id.
     */
    default <R extends RepositoryEntity> Mono<R> findByIdOrDie(String id, Class<R> type) throws ResourceNotFoundException {
        return findByIdOrDie(id)
                .map(type::cast);
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
    Mono<RepositoryEntity> delete(String id);

    /**
     * Applies a function over the content of the specified repository.
     */
    default <T> Mono<T> applyGetMono(String repositoryId,
                                     RepositoryApi.ApiFunction<RepositoryApi, Mono<T>> apiConsumer) throws RepositoryException {
        return this.applyGetMono(repositoryId, RepositoryApi.class, apiConsumer);
    }

    /**
     * Applies a function over the content of the specified repository.
     */
    <A extends RepositoryApi, T> Mono<T> applyGetMono(String repositoryId,
                                                      Class<A> apiType,
                                                      RepositoryApi.ApiFunction<A, Mono<T>> apiConsumer) throws RepositoryException;

    /**
     * Applies a function over the content of the specified repository.
     */
    default <T> Flux<T> applyGetFlux(String repositoryId,
                                     RepositoryApi.ApiFunction<RepositoryApi, Flux<T>> apiConsumer) throws RepositoryException {
        return this.applyGetFlux(repositoryId, RepositoryApi.class, apiConsumer);
    }

    /**
     * Applies a function over the content of the specified repository.
     */
    <A extends RepositoryApi, T> Flux<T> applyGetFlux(String repositoryId,
                                                      Class<A> apiType,
                                                      RepositoryApi.ApiFunction<A, Flux<T>> apiConsumer) throws RepositoryException;

}
