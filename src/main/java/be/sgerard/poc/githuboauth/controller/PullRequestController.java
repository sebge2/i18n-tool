package be.sgerard.poc.githuboauth.controller;

import be.sgerard.poc.githuboauth.service.git.PullRequestManager;
import be.sgerard.poc.githuboauth.model.git.PullRequestStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Sebastien Gerard
 */
@RestController
public class PullRequestController {

    private final PullRequestManager pullRequestManager;

    public PullRequestController(PullRequestManager pullRequestManager) {
        this.pullRequestManager = pullRequestManager;
    }

    @GetMapping("/pull-request")
    public List<Integer> listRequests() throws Exception {
        return pullRequestManager.listRequests();
    }

    @GetMapping("/pull-request/{number}/status")
    public PullRequestStatus getStatus(@PathVariable int number) throws Exception {
        return pullRequestManager.getStatus(number);
    }
}
