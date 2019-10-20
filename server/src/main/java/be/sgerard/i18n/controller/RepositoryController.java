package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.repository.RepositoryDescriptionDto;
import be.sgerard.i18n.service.git.RepositoryAPI;
import be.sgerard.i18n.service.git.RepositoryException;
import be.sgerard.i18n.service.git.RepositoryManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Sebastien Gerard
 */
@RestController
@RequestMapping(path = "/api")
@Api(value = "Controller handling GIT repository.")
public class RepositoryController {

    private final RepositoryManager repositoryManager;

    public RepositoryController(RepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
    }

    @PutMapping(path = "/repository")
    @ApiOperation(value = "Executes an action on the repository.")
    @PreAuthorize("hasRole('ADMIN')")
    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    public void executeRepositoryAction(@RequestParam(name = "do") RepositoryListAction doAction) {
        switch (doAction) {
            case INITIALIZE:
                repositoryManager.initLocalRepository();
                break;
        }
    }

    @GetMapping(path = "/repository")
    @ApiOperation(value = "Returns repository description.")
    public RepositoryDescriptionDto isInitialized() throws RepositoryException {
        return repositoryManager.getDescription();
    }

    @GetMapping("/repository/branch")
    @ApiOperation(value = "Lists all branches found on the repository.")
    public List<String> listBranches() {
        return repositoryManager.open(RepositoryAPI::listRemoteBranches);
    }

    public enum RepositoryListAction {

        INITIALIZE
    }

}
