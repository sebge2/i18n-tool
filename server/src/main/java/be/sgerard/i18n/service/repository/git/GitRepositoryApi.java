package be.sgerard.i18n.service.repository.git;

import be.sgerard.i18n.service.ValidationException;
import be.sgerard.i18n.service.repository.RepositoryApi;
import be.sgerard.i18n.service.repository.RepositoryException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * API abstracting GIT operations. This API must be closed once it's no more used. Once closed,
 * further operations on it are not allowed. This prevent concurrent access to the repository.
 * <p>
 * All the file relative to the root location of the repository.
 *
 * @author Sebastien Gerard
 */
@SuppressWarnings("UnusedReturnValue")
public interface GitRepositoryApi extends RepositoryApi {

    /**
     * Default Git branch.
     */
    String DEFAULT_BRANCH = "master";

    /**
     * Initializes the local repository based on the remote repository.
     */
    GitRepositoryApi init() throws RepositoryException;

    /**
     * Validates that the URI and credentials are valid.
     */
    GitRepositoryApi validateInfo() throws RepositoryException, ValidationException;

    /**
     * Pulls changes from the remote repository branch to the local repository branch.
     */
    GitRepositoryApi pull() throws RepositoryException;

    /**
     * Returns the current Git branch.
     */
    String getCurrentBranch() throws RepositoryException;

    /**
     * Returns all the remote Git branches.
     */
    List<String> listRemoteBranches() throws RepositoryException;

    /**
     * Returns all the local Git branches.
     */
    List<String> listLocalBranches() throws RepositoryException;

    /**
     * Checkouts the specified branch.
     */
    GitRepositoryApi checkout(String branch) throws RepositoryException;

    /**
     * Creates a new branch having the specified name.
     */
    GitRepositoryApi createBranch(String branch) throws RepositoryException;

    /**
     * Removes the branch having the specified name.
     */
    GitRepositoryApi removeBranch(String branch) throws RepositoryException;

    /**
     * Lists recursively all files (normal files and directories) at the specified location.
     */
    Stream<File> listAllFiles(File file) throws RepositoryException;

    /**
     * Lists recursively all normal files (not directories) at the specified location.
     */
    Stream<File> listNormalFiles(File file) throws RepositoryException;

    /**
     * Lists recursively all directories at the specified location.
     */
    Stream<File> listDirectories(File file) throws RepositoryException;

    /**
     * Opens the specified file.
     */
    InputStream openInputStream(File file) throws RepositoryException;

    /**
     * Creates a temporary of the specified file and returns it. Once the API will be closed, the temporary file will be dropped.
     */
    File openAsTemp(File file) throws RepositoryException;

    /**
     * Creates an output stream to the specified file.
     */
    OutputStream openOutputStream(File file) throws RepositoryException;

    /**
     * Reverts the specified file.
     */
    GitRepositoryApi revert(File file) throws RepositoryException;

    /**
     * Commits all the current changes using the specified message.
     */
    GitRepositoryApi commitAll(String message) throws RepositoryException;

    /**
     * Pushes current changes to the remote repository.
     */
    GitRepositoryApi push() throws RepositoryException;

    /**
     * Deletes the current repository.
     */
    GitRepositoryApi delete() throws RepositoryException;

    /**
     * Git configuration.
     */
    final class Configuration {

        private final URI remoteUri;
        private final File repositoryLocation;

        private String defaultBranch = DEFAULT_BRANCH;
        private String username;
        private String password;

        public Configuration(URI remoteUri, File repositoryLocation) {
            this.remoteUri = remoteUri;
            this.repositoryLocation = repositoryLocation;
        }

        /**
         * Returns the remote URI of the repository.
         */
        public URI getRemoteUri() {
            return remoteUri;
        }

        /**
         * Returns the file location of the repository.
         */
        public File getRepositoryLocation() {
            return repositoryLocation;
        }

        /**
         * Returns the default branch.
         */
        public String getDefaultBranch() {
            return defaultBranch;
        }

        /**
         * Sets the default branch.
         */
        public Configuration setDefaultBranch(String defaultBranch) {
            this.defaultBranch = defaultBranch;
            return this;
        }

        /**
         * Returns the current username.
         */
        public Optional<String> getUsername() {
            return Optional.ofNullable(username);
        }

        /**
         * Sets the current username.
         */
        public Configuration setUsername(String username) {
            this.username = username;
            return this;
        }

        /**
         * Returns the password to use.
         */
        public Optional<String> getPassword() {
            return Optional.ofNullable(password);
        }

        /**
         * Sets the password to use.
         */
        public Configuration setPassword(String password) {
            this.password = password;
            return this;
        }

        /**
         * Returns {@link CredentialsProvider credentials} to use.
         */
        public Optional<CredentialsProvider> toCredentialsProvider() {
            return getUsername()
                    .map(username -> new UsernamePasswordCredentialsProvider(username, getPassword().orElse("")));
        }
    }
}
