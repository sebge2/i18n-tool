package be.sgerard.poc.githuboauth.controller;

import be.sgerard.poc.githuboauth.service.git.RepositoryManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Sebastien Gerard
 */
@RestController
public class RepositoryController {

    private final RepositoryManager repositoryManager;

    public RepositoryController(RepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
    }

    @GetMapping("/repository/branch")
    public List<String> listBranches() throws Exception {
        return repositoryManager.listBranches();
    }

}
