package be.sgerard.i18n.service.repository.git;

import be.sgerard.i18n.service.ValidationException;
import be.sgerard.i18n.service.repository.RepositoryApi;
import be.sgerard.i18n.service.repository.RepositoryException;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
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
     * Fetches changes from the remote repository branch to the local repository branch.
     */
    GitRepositoryApi fetch() throws RepositoryException;

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
    GitRepositoryApi checkoutDefaultBranch() throws RepositoryException;

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
    File openAsTemp(File file, boolean createIfNotExists) throws RepositoryException;

    /**
     * Creates an output stream to the specified file.
     */
    OutputStream openOutputStream(File file, boolean createIfNotExists) throws RepositoryException;

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
     * Resets all files to the HEAD version.
     */
    GitRepositoryApi resetHardHead() throws RepositoryException;

    /**
     * Deletes the current repository.
     */
    GitRepositoryApi delete() throws RepositoryException;

    @Override
    void close();

    /**
     * Git configuration.
     */
    @Getter
    @Setter
    @Accessors(chain = true)
    final class Configuration {

        /**
         * The remote URI of the repository.
         */
        private final URI remoteUri;

        /**
         * The file location of the repository.
         */
        private final File repositoryLocation;

        /**
         * The default branch.
         */
        private String defaultBranch = DEFAULT_BRANCH;

        /**
         * The current username.
         */
        private String username;

        /**
         * The password to use.
         */
        private String password;

        /**
         * The display name of the current user.
         */
        private String displayName;

        /**
         * The current user email.
         */
        private String email;

        public Configuration(File repositoryLocation, URI remoteUri) {
            this.remoteUri = remoteUri;
            this.repositoryLocation = repositoryLocation;
        }

        public Configuration(File repositoryLocation) {
            this(repositoryLocation, null);
        }

        /**
         * @see #remoteUri
         */
        public Optional<URI> getRemoteUri() {
            return Optional.ofNullable(remoteUri);
        }

        /**
         * @see #username
         */
        public Optional<String> getUsername() {
            return Optional.ofNullable(username);
        }

        /**
         * @see #password
         */
        public Optional<String> getPassword() {
            return Optional.ofNullable(password);
        }

        /**
         * @see #displayName
         */
        public Optional<String> getDisplayName() {
            return Optional.ofNullable(displayName);
        }

        /**
         * @see #email
         */
        public Optional<String> getEmail() {
            return Optional.ofNullable(email);
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
