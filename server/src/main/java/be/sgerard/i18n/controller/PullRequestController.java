package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.repository.github.dto.GitHubPullRequestDto;
import be.sgerard.i18n.model.repository.github.external.GitHubPullRequestStatus;
import be.sgerard.i18n.service.repository.github.GitHubService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "PullRequest", description = "Controller GitHub pull requests.")
public class PullRequestController {

    private final GitHubService gitHubService;

    public PullRequestController(GitHubService gitHubService) {
        this.gitHubService = gitHubService;
    }

    /**
     * Returns all the pull requests.
     */
    @GetMapping("/github-pull-request")
    @Operation(operationId = "listAllRequests", summary = "List all pull requests.")
    public Flux<GitHubPullRequestDto> listRequests() {
        return gitHubService.findAll();
    }

    /**
     * Returns all the pull requests of the specified repository.
     */
    @GetMapping("/repository/{repositoryId}/github-pull-request")
    @Operation(operationId = "listRequests", summary = "List all pull requests of a repository.")
    public Flux<GitHubPullRequestDto> listRequests(@PathVariable String repositoryId) {
        return gitHubService.findAll(repositoryId);
    }

    /**
     * Returns the status of the pull request having the specified number.
     */
    @GetMapping("/repository/{repositoryId}/github-pull-request/{number}/status")
    @Operation(operationId = "getStatus", summary = "Returns the status of the specified pull request.")
    public Mono<GitHubPullRequestStatus> getStatus(@PathVariable String repositoryId,
                                                   @PathVariable int number) {
        return gitHubService
                .findByNumber(repositoryId, number)
                .map(GitHubPullRequestDto::getStatus);
    }
}
