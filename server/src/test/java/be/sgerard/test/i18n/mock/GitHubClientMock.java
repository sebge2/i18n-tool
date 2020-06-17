package be.sgerard.test.i18n.mock;

import be.sgerard.i18n.model.github.GitHubPullRequestDto;
import be.sgerard.i18n.model.github.GitHubPullRequestStatus;
import be.sgerard.i18n.model.repository.persistence.GitHubRepositoryEntity;
import be.sgerard.i18n.service.client.GitHubClient;
import be.sgerard.i18n.service.repository.RepositoryManager;
import be.sgerard.i18n.service.workspace.WorkspaceException;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static be.sgerard.i18n.support.GitHubUtils.createGitHubRepositoryName;

/**
 * @author Sebastien Gerard
 */
@Service
@Primary
public class GitHubClientMock implements GitHubClient {

    private final Set<PullRequest> pullRequests = new HashSet<>();

    private final RepositoryManager repositoryManager;

    public GitHubClientMock(RepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
    }

    @Override
    public Mono<GitHubPullRequestDto> createRequest(String repositoryId, String message, String currentBranch, String targetBranch) throws WorkspaceException {
        return repositoryManager
                .findByIdOrDie(repositoryId, GitHubRepositoryEntity.class)
                .map(entity -> {
                    final PullRequest pullRequest = new PullRequest(
                            repositoryId,
                            createGitHubRepositoryName(entity.getUsername(), entity.getRepository()),
                            message,
                            currentBranch,
                            targetBranch,
                            pullRequests.stream()
                                    .filter(req -> Objects.equals(req.getRepositoryId(), repositoryId))
                                    .map(PullRequest::getRequestNumber)
                                    .max(Integer::compareTo)
                                    .map(max -> max + 1)
                                    .orElse(0)
                    );

                    pullRequests.add(pullRequest);

                    return pullRequest.toDto();
                });
    }

    @Override
    public Flux<GitHubPullRequestDto> findAll(String repositoryId) throws WorkspaceException {
        return Flux.fromStream(
                pullRequests.stream()
                        .filter(pullRequest -> Objects.equals(pullRequest.getRepositoryId(), repositoryId))
                        .map(PullRequest::toDto)
        );
    }

    @Override
    public Flux<GitHubPullRequestDto> findAll() throws WorkspaceException {
        return Flux.fromStream(pullRequests.stream().map(PullRequest::toDto));
    }

    @Override
    public Mono<GitHubPullRequestDto> findByNumber(String repositoryId, int requestNumber) throws WorkspaceException {
        return Mono.justOrEmpty(
                pullRequests.stream()
                        .filter(pullRequest -> Objects.equals(pullRequest.getRepositoryId(), repositoryId))
                        .filter(pullRequest -> Objects.equals(pullRequest.getRequestNumber(), requestNumber))
                        .findFirst()
                        .map(PullRequest::toDto)
        );
    }

    public static final class PullRequest {

        private final String repositoryId;
        private final String repositoryName;
        private final String message;
        private final String currentBranch;
        private final String targetBranch;
        private final int requestNumber;

        private GitHubPullRequestStatus status = GitHubPullRequestStatus.OPEN;

        public PullRequest(String repositoryId,
                           String repositoryName,
                           String message,
                           String currentBranch,
                           String targetBranch,
                           int requestNumber) {
            this.repositoryId = repositoryId;
            this.repositoryName = repositoryName;
            this.message = message;
            this.currentBranch = currentBranch;
            this.targetBranch = targetBranch;
            this.requestNumber = requestNumber;
        }

        public String getRepositoryId() {
            return repositoryId;
        }

        public String getRepositoryName() {
            return repositoryName;
        }

        public String getMessage() {
            return message;
        }

        public String getCurrentBranch() {
            return currentBranch;
        }

        public String getTargetBranch() {
            return targetBranch;
        }

        public int getRequestNumber() {
            return requestNumber;
        }

        public GitHubPullRequestStatus getStatus() {
            return status;
        }

        public PullRequest setStatus(GitHubPullRequestStatus status) {
            this.status = status;
            return this;
        }

        public GitHubPullRequestDto toDto() {
            return GitHubPullRequestDto.builder()
                    .repositoryName(getRepositoryName())
                    .currentBranch(getCurrentBranch())
                    .targetBranch(getTargetBranch())
                    .number(getRequestNumber())
                    .build();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }

            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            final PullRequest that = (PullRequest) o;

            return requestNumber == that.requestNumber &&
                    Objects.equals(repositoryId, that.repositoryId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(repositoryId, requestNumber);
        }
    }
}
