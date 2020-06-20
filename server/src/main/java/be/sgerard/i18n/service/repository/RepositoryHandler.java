package be.sgerard.i18n.service.repository;

import be.sgerard.i18n.model.repository.RepositoryType;
import be.sgerard.i18n.model.repository.dto.RepositoryCreationDto;
import be.sgerard.i18n.model.repository.dto.RepositoryPatchDto;
import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.model.security.auth.RepositoryCredentials;
import be.sgerard.i18n.service.ValidationException;
import reactor.core.publisher.Mono;

/**
 * Handler of a particular repository.
 *
 * @author Sebastien Gerard
 */
public interface RepositoryHandler<E extends RepositoryEntity, C extends RepositoryCreationDto, P extends RepositoryPatchDto> {

    /**
     * Checks whether the specified {@link RepositoryType type} is supported.
     */
    boolean support(RepositoryType type);

    /**
     * Creates the {@link RepositoryEntity entity} for the specified {@link RepositoryCreationDto DTO}.
     */
    Mono<E> createRepository(C creationDto) throws ValidationException;

    /**
     * Initializes the specified repository.
     */
    Mono<E> initializeRepository(E repository) throws RepositoryException;

    /**
     * Update the specified repository based on the specified {@link RepositoryPatchDto patch}.
     */
    Mono<E> updateRepository(E repository, P patchDto) throws RepositoryException;

    /**
     * Deletes the specified repository.
     */
    Mono<E> deleteRepository(E repository) throws RepositoryException;

    /**
     * Creates the API to use for accessing the specified repository.
     */
    Mono<RepositoryApi> createAPI(E repository) throws RepositoryException;

    /**
     * Returns the {@link RepositoryCredentials default credentials} to use to access the repository (if the authenticated user
     * could not himself access it).
     */
    Mono<RepositoryCredentials> getDefaultCredentials(E repository) throws RepositoryException;
}
