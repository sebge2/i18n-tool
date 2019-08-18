package be.sgerard.poc.githuboauth.controller;

import be.sgerard.poc.githuboauth.service.git.RepositoryAPI;
import be.sgerard.poc.githuboauth.service.git.RepositoryException;
import be.sgerard.poc.githuboauth.service.git.RepositoryManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
    public void executeRepositoryAction(@RequestParam(name = "do") RepositoryListAction doAction) throws RepositoryException {
        switch (doAction) {
            case INITIALIZE:
                repositoryManager.initLocalRepository();
                break;
        }
    }

    @GetMapping(path = "/repository/initialized")
    @ApiOperation(value = "Returns whether the repository has been initialized")
    public boolean isInitialized() {
        return repositoryManager.isInitialized();
    }

    @GetMapping("/repository/branch")
    @ApiOperation(value = "Lists all branches found on the repository.")
    public List<String> listBranches() throws Exception {
        return repositoryManager.open(RepositoryAPI::listRemoteBranches);
    }

    public enum RepositoryListAction {

        INITIALIZE
    }

}
