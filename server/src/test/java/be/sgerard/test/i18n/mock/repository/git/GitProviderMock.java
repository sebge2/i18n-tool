package be.sgerard.test.i18n.mock.repository.git;

import be.sgerard.i18n.service.repository.git.GitRepositoryApi;

import java.net.URI;

/**
 * Mock for a provider of Git repositories.
 *
 * @author Sebastien Gerard
 */
public interface GitProviderMock {

    /**
     * Returns whether the specified repository exists in this provider.
     */
    boolean isRepositoryRegistered(URI repositoryURI);

    /**
     * Opens an {@link GitRepositoryApi API} for simulating local access the repository having the specified configuration.
     *
     * @throws IllegalStateException if the repository does not exist
     */
    GitRepositoryApi openLocalApi(GitRepositoryApi.Configuration configuration);

    /**
     * Stops all mocked repositories (resources will be dropped).
     */
    void stopAll();
}
