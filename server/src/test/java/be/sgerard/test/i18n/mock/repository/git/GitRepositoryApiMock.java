package be.sgerard.test.i18n.mock.repository.git;

import be.sgerard.i18n.model.validation.ValidationResult;
import be.sgerard.i18n.service.repository.RepositoryException;
import be.sgerard.i18n.service.repository.git.DefaultGitRepositoryApi;
import be.sgerard.i18n.service.repository.git.GitRepositoryApi;

import java.util.function.Function;

/**
 * {@link GitRepositoryApi Git Repository API} using a {@link RemoteGitRepositoryMock mock Git repository}.
 *
 * @author Sebastien Gerard
 */
public class GitRepositoryApiMock extends DefaultGitRepositoryApi {

    private final GitRepositoryApi.Configuration originalConfiguration;
    private final Function<Configuration, ValidationResult> configurationValidator;

    public GitRepositoryApiMock(RemoteGitRepositoryMock repository,
                                Configuration originalConfiguration,
                                Function<Configuration, ValidationResult> configurationValidator) {
        super(createMockedConfiguration(repository, originalConfiguration));

        this.originalConfiguration = originalConfiguration;
        this.configurationValidator = configurationValidator;
    }

    @Override
    public ValidationResult validateInfo() throws RepositoryException {
        return configurationValidator.apply(originalConfiguration);
    }

    /**
     * Updates the original configuration and changes the location of the remote repository to a fake one.
     */
    private static Configuration createMockedConfiguration(RemoteGitRepositoryMock repository, Configuration originalConfiguration) {
        return new Configuration(originalConfiguration.getRepositoryLocation(), repository.getLocationUri())
                .setDefaultBranch(originalConfiguration.getDefaultBranch())
                .setUsername(originalConfiguration.getUsername().orElse(null))
                .setPassword(originalConfiguration.getPassword().orElse(null))
                .setDisplayName(originalConfiguration.getDisplayName().orElse(null))
                .setEmail(originalConfiguration.getEmail().orElse(null));
    }
}
