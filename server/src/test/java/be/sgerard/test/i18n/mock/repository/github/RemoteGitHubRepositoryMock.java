package be.sgerard.test.i18n.mock.repository.github;

import be.sgerard.i18n.model.repository.github.GitHubPullRequestCreationInfo;
import be.sgerard.i18n.model.repository.github.GitHubRepositoryId;
import be.sgerard.i18n.model.repository.github.dto.GitHubPullRequestDto;
import be.sgerard.i18n.model.repository.github.external.GitHubPullRequestStatus;
import be.sgerard.i18n.model.validation.ValidationMessage;
import be.sgerard.i18n.model.validation.ValidationResult;
import be.sgerard.i18n.service.repository.git.GitRepositoryApi;
import be.sgerard.test.i18n.mock.repository.git.GitRepositoryApiMock;
import be.sgerard.test.i18n.mock.repository.git.RemoteGitRepositoryMock;
import be.sgerard.test.i18n.helper.repository.git.StepRemoteGitRepositoryBranch;
import be.sgerard.test.i18n.helper.repository.github.StepRemoteGitHubRepositoryPullRequest;

import java.io.File;
import java.net.URI;
import java.util.*;

import static be.sgerard.i18n.service.repository.git.BaseGitRepositoryApi.INVALID_CREDENTIALS;
import static java.util.Collections.unmodifiableSet;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Mock of a remote GitHub repository.
 *
 * @author Sebastien Gerard
 */
public class RemoteGitHubRepositoryMock {

    private final GitHubRepositoryId repositoryId;
    private final Collection<String> accessTokens;

    private final Set<PullRequest> pullRequests = new HashSet<>();
    private final RemoteGitRepositoryMock repositoryMock;

    public RemoteGitHubRepositoryMock(GitHubRepositoryId repositoryId, File repositoryLocation, Collection<String> accessTokens) {
        this.repositoryId = repositoryId;
        this.accessTokens = accessTokens;

        repositoryMock = new RemoteGitRepositoryMock(repositoryLocation);
    }

    /**
     * Starts this mock, all resources will be initialized.
     */
    public RemoteGitHubRepositoryMock start() {
        repositoryMock.init();

        return this;
    }

    /**
     * Stops this mock, all resources will be dropped.
     */
    public void stop() {
        repositoryMock.destroy();
    }

    /**
     * Returns the {@link GitHubRepositoryId id} of the mocked repository.
     */
    public GitHubRepositoryId getRepositoryId() {
        return repositoryId;
    }

    /**
     * Returns the {@link URI} which is mocked by this repository.
     */
    public URI getRemoteURI() {
        return getRepositoryId().toURI();
    }

    /**
     * Returns all the {@link PullRequest pull-requests} of this repository.
     */
    public Set<PullRequest> getPullRequests() {
        return unmodifiableSet(pullRequests);
    }

    /**
     * Finds the pull-request having the specified {@link PullRequest#getRequestNumber() number}.
     */
    public Optional<PullRequest> findPullRequestByNumber(int pullRequestNumber) {
        return getPullRequests().stream()
                .filter(pullRequest -> pullRequest.getRequestNumber() == pullRequestNumber)
                .findFirst();
    }

    /**
     * Finds the pull-request having the specified {@link GitHubPullRequestCreationInfo#getTargetBranch() target branch}.
     */
    public Optional<PullRequest> findPullRequestByTargetBranch(String targetBranch) {
        return pullRequests.stream()
                .filter(request -> Objects.equals(targetBranch, request.getCreationInfo().getTargetBranch()))
                .findFirst();
    }

    /**
     * Returns whether the specified token is allowed to access this repository.
     */
    public boolean isAllowed(String token) {
        return accessTokens.contains(token);
    }

    /**
     * Opens an {@link GitRepositoryApi API} for simulating local access to this mock remote repository.
     */
    public GitRepositoryApi openLocalApi(GitRepositoryApi.Configuration configuration) {
        return new GitRepositoryApiMock(repositoryMock, configuration, this::validateConfiguration);
    }

    /**
     * Returns the {@link StepRemoteGitRepositoryBranch helper} for managing the state of the remote Git repository.
     */
    public StepRemoteGitRepositoryBranch manageRemoteBranches() {
        return new StepRemoteGitRepositoryBranch(repositoryMock);
    }

    /**
     * Returns the {@link StepRemoteGitHubRepositoryPullRequest helper} for managing the state of pull-requests.
     */
    public StepRemoteGitHubRepositoryPullRequest managePullRequests() {
        return new StepRemoteGitHubRepositoryPullRequest(this);
    }

    /**
     * Creates a new {@link PullRequest pull request} with the specified {@link GitHubPullRequestCreationInfo creation-info}.
     */
    public PullRequest createPullRequest(GitHubPullRequestCreationInfo creationInfo) {
        final PullRequest pullRequest = new PullRequest(creationInfo, pullRequests.size() + 1);

        pullRequests.add(pullRequest);

        return pullRequest;
    }

    /**
     * Closes all pull-requests.
     */
    public void closeAllPullRequests() {
        getPullRequests().forEach(this::closePullRequest);
    }

    /**
     * Closes the specified pull-request.
     */
    public void closePullRequest(PullRequest pullRequest) {
        try (GitRepositoryApi api = repositoryMock.openApi()) {
            api
                    .checkout(pullRequest.getTargetBranch())
                    .merge(pullRequest.getCurrentBranch());
        }

        pullRequest.close();
    }

    /**
     * Validates that the specified configuration is correct.
     */
    private ValidationResult validateConfiguration(GitRepositoryApi.Configuration configuration) {
        if (!isEmpty(configuration.getPassword().orElse(""))
                || isEmpty(configuration.getUsername().orElse(""))
                || !accessTokens.contains(configuration.getUsername().orElse(null))) {
            return ValidationResult.builder()
                    .messages(new ValidationMessage(INVALID_CREDENTIALS, configuration.getRemoteUri()))
                    .build();
        } else {
            return ValidationResult.EMPTY;
        }
    }

    /**
     * A mocked pull-request.
     */
    public final class PullRequest {

        private final GitHubPullRequestCreationInfo creationInfo;
        private final int requestNumber;

        private GitHubPullRequestStatus status = GitHubPullRequestStatus.OPEN;

        public PullRequest(GitHubPullRequestCreationInfo creationInfo, int requestNumber) {
            this.creationInfo = creationInfo;
            this.requestNumber = requestNumber;
        }

        /**
         * Returns the {@link GitHubRepositoryId id} of the mocked repository.
         */
        public GitHubRepositoryId getRepositoryId() {
            return repositoryId;
        }

        /**
         * Returns the original {@link GitHubPullRequestCreationInfo info} used to create this pull-request.
         */
        public GitHubPullRequestCreationInfo getCreationInfo() {
            return creationInfo;
        }

        /**
         * Returns the branch to merge.
         */
        public String getCurrentBranch() {
            return getCreationInfo().getCurrentBranch();
        }

        /**
         * Returns the branch targets of the merge.
         */
        public String getTargetBranch() {
            return getCreationInfo().getTargetBranch();
        }

        /**
         * Returns the unique number of this pull-request in this repository.
         */
        public int getRequestNumber() {
            return requestNumber;
        }

        /**
         * Returns the current {@link GitHubPullRequestStatus status}.
         */
        public GitHubPullRequestStatus getStatus() {
            return status;
        }

        /**
         * Returns the {@link GitHubPullRequestDto DTO} representing this pull-request.
         */
        public GitHubPullRequestDto toDto() {
            return GitHubPullRequestDto.builder()
                    .repositoryName(getRepositoryId().toFullName())
                    .currentBranch(getCreationInfo().getCurrentBranch())
                    .targetBranch(getCreationInfo().getTargetBranch())
                    .number(getRequestNumber())
                    .status(getStatus())
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

            return Objects.equals(getRequestNumber(), that.getRequestNumber()) && Objects.equals(getRepositoryId(), that.getRepositoryId());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getRepositoryId(), getRequestNumber());
        }

        /**
         * Closes this pull-request.
         */
        private void close() {
            this.status = GitHubPullRequestStatus.CLOSED;
        }
    }
}
