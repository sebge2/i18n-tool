package be.sgerard.i18n.service.repository;

import be.sgerard.i18n.model.repository.RepositoryType;
import be.sgerard.i18n.model.repository.dto.RepositoryCreationDto;
import be.sgerard.i18n.model.repository.dto.RepositoryPatchDto;
import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.model.security.repository.RepositoryCredentials;
import reactor.core.publisher.Mono;

/**
 * Handler of a particular repository.
 *
 * @author Sebastien Gerard
 */
public interface RepositoryHandler<E extends RepositoryEntity, C extends RepositoryCreationDto, P extends RepositoryPatchDto, D extends RepositoryCredentials> {

    /**
     * Checks whether the specified {@link RepositoryType type} is supported.
     */
    boolean support(RepositoryType type);

    /**
     * Creates the {@link RepositoryEntity entity} for the specified {@link RepositoryCreationDto DTO}.
     */
    Mono<E> createRepository(C creationDto);

    /**
     * Initializes the specified repository.
     */
    Mono<E> initializeRepository(E repository, D credentials) throws RepositoryException;

    /**
     * Update the specified repository based on the specified {@link RepositoryPatchDto patch}.
     */
    Mono<E> updateRepository(E repository, P patchDto, D credentials) throws RepositoryException;

    /**
     * Deletes the specified repository.
     */
    Mono<E> deleteRepository(E repository, D credentials) throws RepositoryException;

    /**
     * Initializes the API to use for accessing in runtime the specified repository.
     */
    Mono<RepositoryApi> initApi(E repository, D credentials) throws RepositoryException;
}
