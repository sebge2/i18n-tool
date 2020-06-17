package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.github.GitHubPullRequestDto;
import be.sgerard.i18n.model.github.GitHubPullRequestStatus;
import be.sgerard.i18n.service.client.GitHubClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Controller handling Github pull requests.
 *
 * @author Sebastien Gerard
 */
@RestController
@RequestMapping(path = "/api")
@Api(value = "Controller GitHub pull requests.")
public class PullRequestController {

    private final GitHubClient pullRequestManager;

    public PullRequestController(GitHubClient pullRequestManager) {
        this.pullRequestManager = pullRequestManager;
    }

    /**
     * Returns all the pull requests.
     */
    @GetMapping("/github-pull-request")
    @ApiOperation(value = "List all pull requests.")
    public Flux<GitHubPullRequestDto> listRequests() {
        return pullRequestManager.findAll();
    }

    /**
     * Returns all the pull requests of the specified repository.
     */
    @GetMapping("/repository/{repositoryId}/github-pull-request")
    @ApiOperation(value = "List all pull requests of a repository.")
    public Flux<GitHubPullRequestDto> listRequests(@PathVariable String repositoryId) {
        return pullRequestManager.findAll(repositoryId);
    }

    /**
     * Returns the status of the pull request having the specified number.
     */
    @GetMapping("/repository/{repositoryId}/github-pull-request/{number}/status")
    @ApiOperation(value = "Returns the status of the specified pull request.")
    public Mono<GitHubPullRequestStatus> getStatus(@PathVariable String repositoryId,
                                                   @PathVariable int number) {
        return pullRequestManager
                .findByNumber(repositoryId, number)
                .map(GitHubPullRequestDto::getStatus);
    }
}
