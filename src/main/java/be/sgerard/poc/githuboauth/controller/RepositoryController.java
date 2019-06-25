package be.sgerard.poc.githuboauth.controller;

import be.sgerard.poc.githuboauth.service.PullRequestManager;
import be.sgerard.poc.githuboauth.service.RepositoryManager;
import com.google.common.io.Files;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Sebastien Gerard
 */
@RestController
public class RepositoryController {

    private final RepositoryManager repositoryManager;
    private final PullRequestManager pullRequestManager;

    public RepositoryController(RepositoryManager repositoryManager, PullRequestManager pullRequestManager) {
        this.repositoryManager = repositoryManager;
        this.pullRequestManager = pullRequestManager;
    }

    @GetMapping("/repository/branch")
    public List<String> listBranches() throws Exception {
        repositoryManager.initializeLocalRepo(Files.createTempDir());

        return repositoryManager.listBranches();
    }


    @GetMapping("/pull-request")
    public List<Integer> listRequests() throws Exception {
        return pullRequestManager.listRequests();
    }


    @GetMapping("/pull-request/{number}/status")
    public String getStatus(@PathVariable int number) throws Exception {
        return pullRequestManager.getStatus(number);
    }

    @GetMapping("/toto")
    public int createPR() throws Exception {
        return pullRequestManager.createRequest("test message", "test", "master");
    }
}
