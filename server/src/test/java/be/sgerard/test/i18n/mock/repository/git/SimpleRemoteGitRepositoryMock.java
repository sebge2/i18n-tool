package be.sgerard.test.i18n.mock.repository.git;

import be.sgerard.i18n.model.validation.ValidationMessage;
import be.sgerard.i18n.model.validation.ValidationResult;
import be.sgerard.i18n.service.repository.git.GitRepositoryApi;
import be.sgerard.test.i18n.helper.repository.git.StepRemoteGitRepositoryBranch;
import lombok.Value;

import java.io.File;
import java.net.URI;
import java.util.Collection;

import static be.sgerard.i18n.service.repository.git.BaseGitRepositoryApi.INVALID_CREDENTIALS;

/**
 * Mock of a simple remote Git repository.
 *
 * @author Sebastien Gerard
 */
public class SimpleRemoteGitRepositoryMock {

    private final URI remoteURI;
    private final Collection<UserCredentials> userCredentials;
    private final RemoteGitRepositoryMock repositoryMock;

    public SimpleRemoteGitRepositoryMock(URI remoteURI, File repositoryLocation, Collection<UserCredentials> userCredentials) {
        this.remoteURI = remoteURI;
        this.userCredentials = userCredentials;
        this.repositoryMock = new RemoteGitRepositoryMock(repositoryLocation);
    }

    /**
     * Starts this mock, all resources will be initialized.
     */
    public SimpleRemoteGitRepositoryMock start() {
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
     * Returns the {@link URI} which is mocked by this repository.
     */
    public URI getRemoteURI() {
        return remoteURI;
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
     * Validates that the specified configuration is correct.
     */
    private ValidationResult validateConfiguration(GitRepositoryApi.Configuration configuration) {
        if (!userCredentials.contains(new UserCredentials(configuration.getUsername().orElse(""), configuration.getPassword().orElse("")))) {
            return ValidationResult.builder()
                    .messages(new ValidationMessage(INVALID_CREDENTIALS, configuration.getRemoteUri()))
                    .build();
        } else {
            return ValidationResult.EMPTY;
        }
    }

    /**
     * Credentials of a user.
     */
    @Value
    @SuppressWarnings("RedundantModifiersValueLombok")
    public static final class UserCredentials {

        private final String username;
        private final String password;

    }
}
