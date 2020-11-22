package be.sgerard.test.i18n.mock.repository.git;

import be.sgerard.i18n.model.repository.dto.GitRepositoryCreationDto;
import be.sgerard.i18n.service.repository.git.GitRepositoryApi;
import be.sgerard.i18n.support.FileUtils;
import org.springframework.stereotype.Component;

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
public class SimpleGitProviderMock implements GitProviderMock {

    private final Map<String, SimpleRemoteGitRepositoryMock> mocksPerHints = new LinkedHashMap<>();

    public SimpleGitProviderMock() {
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
    public StepGitSetup create(GitRepositoryCreationDto creationDto, String hint) {
        return new StepGitSetup(creationDto.getLocationAsURI(), hint);
    }

    /**
     * Finds the repository having the specified hint.
     */
    public SimpleRemoteGitRepositoryMock forHint(String hint) {
        return Optional
                .ofNullable(mocksPerHints.get(hint))
                .orElseThrow(() -> new IllegalArgumentException("There is no hint [" + hint + "]."));
    }

    /**
     * Setup phase of a new Git repository.
     */
    public class StepGitSetup {

        private final URI locationUri;
        private final String hint;
        private final Collection<SimpleRemoteGitRepositoryMock.UserCredentials> userCredentials = new HashSet<>();

        private File repositoryLocation;

        public StepGitSetup(URI locationUri, String hint) {
            this.locationUri = locationUri;
            this.hint = hint;
        }

        /**
         * Repository files will be based on the current Git project.
         */
        public StepGitSetup onCurrentGitProject() {
            return basedOnGitDirectory(currentProjectLocation());
        }

        /**
         * Repository files will be based on specified directory.
         */
        public StepGitSetup basedOnGitDirectory(File originalDirectory) {
            this.repositoryLocation = originalDirectory;
            return this;
        }

        /**
         * Adds a user having the specified username.
         */
        public StepGitSetup addUser(String username){
            return addUser(username, null);
        }

        /**
         * Adds a user having the specified username and password.
         */
        public StepGitSetup addUser(String username, String password){
            this.userCredentials.add(new SimpleRemoteGitRepositoryMock.UserCredentials(username, password));
            return this;
        }

        /**
         * Starts the repository, resources will be initialized.
         */
        @SuppressWarnings("UnusedReturnValue")
        public SimpleRemoteGitRepositoryMock start() {
            final SimpleRemoteGitRepositoryMock mock = new SimpleRemoteGitRepositoryMock(
                    locationUri,
                    (repositoryLocation != null) ? repositoryLocation : FileUtils.createTempDirectory("git-remote-repository-mock-"),
                    userCredentials
            );

            mocksPerHints.put(hint, mock);

            mock.start();

            return mock;
        }
    }
}
