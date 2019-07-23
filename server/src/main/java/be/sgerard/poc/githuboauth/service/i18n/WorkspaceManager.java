package be.sgerard.poc.githuboauth.service.i18n;

import be.sgerard.poc.githuboauth.model.i18n.persistence.WorkspaceEntity;
import be.sgerard.poc.githuboauth.service.LockTimeoutException;
import be.sgerard.poc.githuboauth.service.ResourceNotFoundException;
import be.sgerard.poc.githuboauth.service.git.RepositoryException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Sebastien Gerard
 */
public interface WorkspaceManager {

    List<WorkspaceEntity> findWorkspaces() throws RepositoryException, LockTimeoutException;

    List<WorkspaceEntity> getWorkspaces();

    Optional<WorkspaceEntity> getWorkspace(String id);

    WorkspaceEntity initialize(String workspaceId) throws LockTimeoutException, RepositoryException, ResourceNotFoundException;

    WorkspaceEntity startReviewing(String workspaceId, String message) throws ResourceNotFoundException, LockTimeoutException, RepositoryException;

    // TODO endReview()

    void updateTranslations(String workspaceId, Map<String, String> translations) throws ResourceNotFoundException;

    void deleteWorkspace(String workspaceId) throws RepositoryException, LockTimeoutException;

}
