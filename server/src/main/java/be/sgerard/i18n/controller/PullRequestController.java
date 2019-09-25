package be.sgerard.i18n.controller;

import be.sgerard.i18n.service.git.PullRequestManager;
import be.sgerard.i18n.model.git.PullRequestStatus;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Sebastien Gerard
 */
@RestController
@RequestMapping(path = "/api")
@Api(value="Controller GitHub pull requests.")
public class PullRequestController {

    private final PullRequestManager pullRequestManager;

    public PullRequestController(PullRequestManager pullRequestManager) {
        this.pullRequestManager = pullRequestManager;
    }

    @GetMapping("/pull-request")
    @ApiOperation(value = "List all pull requests.")
    public List<Integer> listRequests() throws Exception {
        return pullRequestManager.listRequests();
    }

    @GetMapping("/pull-request/{number}/status")
    @ApiOperation(value = "Returns the status of the specified pull request.")
    public PullRequestStatus getStatus(@PathVariable int number) throws Exception {
        return pullRequestManager.getStatus(number);
    }
}
