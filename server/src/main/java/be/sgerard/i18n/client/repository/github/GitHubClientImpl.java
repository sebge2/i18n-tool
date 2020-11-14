package be.sgerard.i18n.client.repository.github;

import be.sgerard.i18n.model.repository.github.GitHubPullRequestCreationInfo;
import be.sgerard.i18n.model.repository.github.GitHubRepositoryId;
import be.sgerard.i18n.model.repository.github.dto.GitHubPullRequestDto;
import be.sgerard.i18n.model.repository.github.external.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Implementation of the {@link GitHubClient GitHub client}.
 *
 * @author Sebastien Gerard
 */
@Service
public class GitHubClientImpl implements GitHubClient {

    public GitHubClientImpl() {
    }

    @Override
    public Flux<GitHubRepositoryId> findAllRepositories(String token) {
        return initializeClient(token)
                .get()
                .uri("/user/repos")
                .retrieve()
                .bodyToFlux(GitHubRepositoryDto.class)
                .map(repo -> new GitHubRepositoryId(repo.getOwner().getLogin(), repo.getName()));
    }

    @Override
    public Flux<String> findAllOrganizations(String token) {
        return initializeClient(token)
                .get()
                .uri("/user/orgs")
                .retrieve()
                .bodyToFlux(GitHubOrganizationDto.class)
                .map(GitHubOrganizationDto::getLogin);
    }

    @Override
    public Mono<GitHubPullRequestDto> createRequest(GitHubRepositoryId repositoryId, GitHubPullRequestCreationInfo creationInfo, String token) {
        return initializeClient(token)
                .post()
                .uri("/repos/{owner}/{repo}/pulls", repositoryId.getUsername(), repositoryId.getRepositoryName())
                .bodyValue(GitHubPullRequestCreationDto.toDto(creationInfo))
                .retrieve()
                .bodyToMono(be.sgerard.i18n.model.repository.github.external.GitHubPullRequestDto.class)
                .map(be.sgerard.i18n.model.repository.github.external.GitHubPullRequestDto::fromDto);
    }

    @Override
    public Flux<GitHubPullRequestDto> findAll(GitHubRepositoryId repositoryId, String token) {
        return initializeClient(token)
                .get()
                .uri("/repos/{owner}/{repo}/pulls", repositoryId.getUsername(), repositoryId.getRepositoryName())
                .retrieve()
                .bodyToFlux(be.sgerard.i18n.model.repository.github.external.GitHubPullRequestDto.class)
                .map(be.sgerard.i18n.model.repository.github.external.GitHubPullRequestDto::fromDto);
    }

    @Override
    public Mono<GitHubPullRequestDto> findByNumber(GitHubRepositoryId repositoryId, int requestNumber, String token) {
        return initializeClient(token)
                .get()
                .uri("/repos/{owner}/{repo}/pulls/{number}", repositoryId.getUsername(), repositoryId.getRepositoryName(), requestNumber)
                .retrieve()
                .bodyToMono(be.sgerard.i18n.model.repository.github.external.GitHubPullRequestDto.class)
                .map(be.sgerard.i18n.model.repository.github.external.GitHubPullRequestDto::fromDto);
    }

    @Override
    public Mono<String> getCurrentUserLogin(String token) {
        return initializeClient(token)
                .get()
                .uri("/user")
                .retrieve()
                .bodyToMono(GitHubUserDto.class)
                .map(GitHubUserDto::getLogin);
    }

    @Override
    public Mono<Boolean> isRepoMember(GitHubRepositoryId repositoryId, String token) {
        return this
                .getCurrentUserLogin(token)
                .flatMap(currentUser ->
                        initializeClient(token)
                                .get()
                                .uri("/repos/{owner}/{repo}/collaborators/{username}/permission", repositoryId.getUsername(), repositoryId.getRepositoryName(), currentUser)
                                .retrieve()
                                .bodyToMono(GitHubRepositoryCollaboratorPermissionDto.class)
                                .map(GitHubRepositoryCollaboratorPermissionDto::canWrite)
                )
                .onErrorResume(cause -> {
                    if (cause instanceof WebClientResponseException.NotFound) {
                        return Mono.just(false);
                    }

                    return Mono.error(cause);
                });
    }

    /**
     * Initializes the {@link WebClient client} interacting with the GitHub API.
     */
    private WebClient initializeClient(String token) {
        return WebClient
                .builder()
                .baseUrl("https://api.github.com")
                .filter(ExchangeFilterFunctions.basicAuthentication(token, ""))
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

}
