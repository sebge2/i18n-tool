package be.sgerard.poc.githuboauth.controller;

import be.sgerard.poc.githuboauth.model.i18n.TranslationWorkspaceEntity;
import be.sgerard.poc.githuboauth.service.LockTimeoutException;
import be.sgerard.poc.githuboauth.service.ResourceNotFoundException;
import be.sgerard.poc.githuboauth.service.git.RepositoryException;
import be.sgerard.poc.githuboauth.service.i18n.TranslationManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Sebastien Gerard
 */
@RestController
public class WorkspaceController {

    private final TranslationManager translationManager;

    public WorkspaceController(TranslationManager translationManager) {
        this.translationManager = translationManager;
    }

    @GetMapping("/workspace")
    public List<TranslationWorkspaceEntity> getWorkspaces() {
        return translationManager.getWorkspaces();
    }

    @RequestMapping(name = "/workspace", method = RequestMethod.PUT)
    @Async
    public void loadWorkspace(@RequestAttribute(name = "do") WorkspaceAction doAction) throws LockTimeoutException, RepositoryException {
        switch (doAction) {
            case SCAN:
                translationManager.scanBranches();
                break;
        }
    }

    @GetMapping("/workspace/{id}")
    public TranslationWorkspaceEntity getWorkspace(@PathVariable String id) {
        return translationManager.getWorkspace(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));
    }

    public enum WorkspaceAction {

        SCAN
    }
}
