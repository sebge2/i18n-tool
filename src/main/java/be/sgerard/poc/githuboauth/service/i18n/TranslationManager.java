package be.sgerard.poc.githuboauth.service.i18n;

import be.sgerard.poc.githuboauth.model.i18n.TranslationWorkspaceEntity;
import be.sgerard.poc.githuboauth.service.LockTimeoutException;
import be.sgerard.poc.githuboauth.service.ResourceNotFoundException;
import be.sgerard.poc.githuboauth.service.git.RepositoryException;

import java.util.List;
import java.util.Optional;

/**
 * @author Sebastien Gerard
 */
public interface TranslationManager {

    void scanBranches() throws RepositoryException, LockTimeoutException;

    // TODO with request + simplified
    List<TranslationWorkspaceEntity> getWorkspaces();

    // TODO dto
    Optional<TranslationWorkspaceEntity> getWorkspace(String id);

    void loadTranslations(String workspaceId) throws LockTimeoutException, RepositoryException, TranslationLoadingException, ResourceNotFoundException;

    // get traduction
    // refresh une branche => collisions
    // push une branche

}
