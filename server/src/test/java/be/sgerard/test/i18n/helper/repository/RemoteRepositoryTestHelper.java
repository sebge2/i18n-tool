package be.sgerard.test.i18n.helper.repository;

import be.sgerard.i18n.service.repository.git.GitRepositoryApi;
import be.sgerard.test.i18n.mock.repository.git.GitProviderMock;
import be.sgerard.test.i18n.mock.repository.git.InvalidGitRepositoryApi;
import be.sgerard.test.i18n.mock.repository.git.SimpleGitProviderMock;
import be.sgerard.test.i18n.mock.repository.github.GitHubMock;
import org.springframework.stereotype.Component;

import java.util.Collection;

import static java.util.Arrays.asList;

/**
 * Helper for mocking remote repositories.
 *
 * @author Sebastien Gerard
 */
@Component
public class RemoteRepositoryTestHelper {

    private final GitHubMock gitHub;
    private final SimpleGitProviderMock git;
    private final Collection<GitProviderMock> providers;

    public RemoteRepositoryTestHelper(GitHubMock gitHub, SimpleGitProviderMock git) {
        this.gitHub = gitHub;
        this.git = git;
        this.providers = asList(gitHub, git);
    }

    /**
     * Returns the helper for {@link GitHubMock GitHub}.
     */
    public GitHubMock gitHub() {
        return gitHub;
    }

    /**
     * Returns the helper for {@link SimpleGitProviderMock Git}.
     */
    public SimpleGitProviderMock git() {
        return git;
    }

    /**
     * Returns all the registered {@link GitProviderMock providers}.
     */
    public Collection<GitProviderMock> getProviders() {
        return providers;
    }

    /**
     * Returns the API for the specified configuration.
     */
    public GitRepositoryApi openApi(GitRepositoryApi.Configuration configuration) {
        return getProviders().stream()
                .filter(provider -> provider.isRepositoryRegistered(configuration.getRemoteUri()))
                .findFirst()
                .map(provider -> provider.openLocalApi(configuration))
                .orElseGet(() -> InvalidGitRepositoryApi.createApiInvalidUri(configuration.getRemoteUri()));
    }

    /**
     * Stops all mocked repositories (resources will be dropped).
     */
    @SuppressWarnings("UnusedReturnValue")
    public RemoteRepositoryTestHelper stopAll() {
        getProviders().forEach(GitProviderMock::stopAll);
        return this;
    }

}
