package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.git.PullRequestStatus;
import be.sgerard.i18n.service.git.PullRequestManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller handling Github pull requests.
 *
 * @author Sebastien Gerard
 */
@RestController
@RequestMapping(path = "/api")
@Api(value = "Controller GitHub pull requests.")
public class PullRequestController {

    private final PullRequestManager pullRequestManager;

    public PullRequestController(PullRequestManager pullRequestManager) {
        this.pullRequestManager = pullRequestManager;
    }

    /**
     * Returns all the current pull requests.
     */
    @GetMapping("/pull-request")
    @ApiOperation(value = "List all pull requests.")
    @PreAuthorize("hasRole('MEMBER_OF_REPOSITORY')")
    public List<Integer> listRequests() {
        return pullRequestManager.listRequests();
    }

    /**
     * Returns the status of the pull request having the specified number.
     */
    @GetMapping("/pull-request/{number}/status")
    @ApiOperation(value = "Returns the status of the specified pull request.")
    @PreAuthorize("hasRole('MEMBER_OF_REPOSITORY')")
    public PullRequestStatus getStatus(@PathVariable int number) {
        return pullRequestManager.getStatus(number);
    }
}
