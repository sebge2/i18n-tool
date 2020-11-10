package be.sgerard.test.i18n.mock.repository.github;

import be.sgerard.i18n.model.repository.dto.GitHubRepositoryCreationDto;
import be.sgerard.i18n.model.repository.github.GitHubRepositoryId;
import be.sgerard.i18n.service.repository.git.GitRepositoryApi;
import be.sgerard.i18n.support.FileUtils;
import be.sgerard.test.i18n.mock.repository.git.GitProviderMock;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.net.URI;
import java.util.*;

import static be.sgerard.test.i18n.support.TestUtils.currentProjectLocation;

/**
 * {@link GitProviderMock Mock} of GitHub.
 *
 * @author Sebastien Gerard
 */
@Component
public class GitHubMock implements GitProviderMock {

    private final Map<String, RemoteGitHubRepositoryMock> mocksPerHints = new LinkedHashMap<>();

    public GitHubMock() {
    }

    @Override
    public boolean isRepositoryRegistered(URI repositoryURI) {
        return mocksPerHints.values().stream().anyMatch(repository -> Objects.equals(repository.getRemoteURI(), repositoryURI));
    }

    @Override
    public GitRepositoryApi openLocalApi(GitRepositoryApi.Configuration configuration) {
        return mocksPerHints.values().stream()
                .filter(repository -> Objects.equals(repository.getRemoteURI(), configuration.getRemoteUri()))
                .findFirst()
                .map(repository -> repository.openLocalApi(configuration))
                .orElseThrow(() -> new IllegalStateException("The specified configuration does not target an existing repository."));
    }

    @Override
    public void stopAll() {
        this.mocksPerHints.forEach((repositoryId, repository) -> repository.stop());
        this.mocksPerHints.clear();
    }

    /**
     * Starts the creation of a new GitHub repository.
     */
    public StepGitHubSetup create(GitHubRepositoryCreationDto creationDto, String hint) {
        return new StepGitHubSetup(new GitHubRepositoryId(creationDto.getUsername(), creationDto.getRepository()), hint);
    }

    /**
     * Searches all the repositories accessible by the specified token.
     */
    public Flux<RemoteGitHubRepositoryMock> findAllRepositories(String token) {
        return Flux.fromIterable(mocksPerHints.values())
                .filter(repository -> repository.isAllowed(token));
    }

    /**
     * Finds the repository having the specified id and accessible by the specified token.
     */
    public Mono<RemoteGitHubRepositoryMock> findRepository(GitHubRepositoryId repositoryId, String token) {
        return findAllRepositories(token)
                .filter(repository -> Objects.equals(repository.getRepositoryId(), repositoryId))
                .next()
                .switchIfEmpty(Mono.error(new RuntimeException("The token [" + token + "] is not allowed")));
    }

    /**
     * Finds the repository having the specified hint.
     */
    public RemoteGitHubRepositoryMock forHint(String hint) {
        return Optional
                .ofNullable(mocksPerHints.get(hint))
                .orElseThrow(() -> new IllegalArgumentException("There is no hint [" + hint + "]."));
    }

    /**
     * Setup phase of a new GitHub repository.
     */
    public class StepGitHubSetup {

        private final GitHubRepositoryId repositoryId;
        private final String hint;
        private final Collection<String> accessTokens = new HashSet<>();

        private File repositoryLocation;

        public StepGitHubSetup(GitHubRepositoryId repositoryId, String hint) {

            this.repositoryId = repositoryId;
            this.hint = hint;
        }

        /**
         * Adds the specified token as a valid token for accessing the repository.
         */
        public StepGitHubSetup accessToken(String accessToken) {
            accessTokens.add(accessToken);
            return this;
        }

        /**
         * Repository files will be based on the current Git project.
         */
        public StepGitHubSetup onCurrentGitProject() {
            return basedOnGitDirectory(currentProjectLocation());
        }

        /**
         * Repository files will be based on specified directory.
         */
        public StepGitHubSetup basedOnGitDirectory(File originalDirectory) {
            this.repositoryLocation = originalDirectory;
            return this;
        }

        /**
         * Starts the repository, resources will be initialized.
         */
        @SuppressWarnings("UnusedReturnValue")
        public RemoteGitHubRepositoryMock start() {
            final RemoteGitHubRepositoryMock mock = new RemoteGitHubRepositoryMock(
                    repositoryId,
                    (repositoryLocation != null) ? repositoryLocation : FileUtils.createTempDirectory("git-hub-remote-repository-mock"),
                    accessTokens
            );

            mocksPerHints.put(hint, mock);

            mock.start();

            return mock;
        }
    }
}
