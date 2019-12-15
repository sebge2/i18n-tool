package be.sgerard.i18n.service.i18n;

import be.sgerard.i18n.model.i18n.dto.WorkspaceDto;
import be.sgerard.i18n.model.i18n.persistence.BundleKeyTranslationEntity;
import be.sgerard.i18n.model.i18n.persistence.WorkspaceEntity;
import be.sgerard.i18n.service.LockTimeoutException;
import be.sgerard.i18n.service.ResourceNotFoundException;
import be.sgerard.i18n.service.repository.RepositoryException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Manager of {@link WorkspaceEntity workspaces}.
 *
 * @author Sebastien Gerard
 */
public interface WorkspaceManager {

    /**
     * Finds all {@link WorkspaceDto workspaces}.
     */
    Flux<WorkspaceEntity> findAll();

    /**
     * Finds all {@link WorkspaceDto workspaces} associated to the specified repository.
     */
    Flux<WorkspaceEntity> findAll(String repositoryId);

    /**
     * Returns the {@link WorkspaceEntity workspace} having the specified id.
     */
    Mono<WorkspaceEntity> findById(String id);

    /**
     * Returns the {@link WorkspaceEntity workspace} having the specified id.
     */
    default Mono<WorkspaceEntity> findByIdOrDie(String id) throws ResourceNotFoundException {
        return findById(id)
                .switchIfEmpty(Mono.error(ResourceNotFoundException.workspaceNotFoundException(id)));
    }

    /**
     * Synchronizes the current workspaces with the specified repository: missing ones are created, workspaces
     * that are no more relevant (branch does not exist anymore) are deleted.
     */
    Flux<WorkspaceEntity> synchronize(String repositoryId) throws RepositoryException, LockTimeoutException;

    /**
     * Initializes the {@link WorkspaceEntity workspace} having the specified id and returns it.
     */
    Mono<WorkspaceEntity> initialize(String workspaceId) throws LockTimeoutException, RepositoryException, ResourceNotFoundException;

    /**
     * Publishes all the modifications made on the specified workspace. Based on the type of repository, a review may start afterwards.
     */
    Mono<WorkspaceEntity> publish(String workspaceId, String message) throws ResourceNotFoundException, LockTimeoutException, RepositoryException;

    /**
     * Updates the specified translations of the specified workspace. Translations are associated with their
     * {@link BundleKeyTranslationEntity#getId() ids}.
     */
    Mono<Void> updateTranslations(String workspaceId, Map<String, String> translations) throws ResourceNotFoundException;

    /**
     * Removes the {@link WorkspaceEntity workspace} having the specified id.
     */
    Mono<Void> delete(String id);

}
