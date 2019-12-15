package be.sgerard.i18n.service.repository.git;

import be.sgerard.i18n.service.ValidationException;
import be.sgerard.i18n.service.repository.RepositoryException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Stream;

/**
 * API abstracting GIT operations. This API must be closed once it's no more used. Once closed,
 * further operations on it are not allowed. This prevent concurrent access to the repository.
 * <p>
 * All the file relative to the root location of the repository.
 *
 * @author Sebastien Gerard
 */
public interface GitAPI extends AutoCloseable {

    /**
     * Default Git branch.
     */
    String DEFAULT_BRANCH = "master";

    /**
     * Initializes the local repository based on the remote repository.
     */
    GitAPI init() throws RepositoryException;

    /**
     * Validates that the URI and credentials are valid.
     */
    GitAPI validateInfo() throws RepositoryException, ValidationException;

    /**
     * Updates the local repository with the remote repository.
     */
    GitAPI update() throws RepositoryException;

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
    GitAPI checkout(String branch) throws RepositoryException;

    /**
     * Creates a new branch having the specified name.
     */
    GitAPI createBranch(String branch) throws RepositoryException;

    /**
     * Removes the branch having the specified name.
     */
    GitAPI removeBranch(String branch) throws RepositoryException;

    /**
     * Lists recursively all files (normal files and directories) at the specified location.
     */
    Stream<File> listAllFiles(File file) throws IOException;

    /**
     * Lists recursively all normal files (not directories) at the specified location.
     */
    Stream<File> listNormalFiles(File file) throws IOException;

    /**
     * Lists recursively all directories at the specified location.
     */
    Stream<File> listDirectories(File file) throws IOException;

    /**
     * Opens the specified file.
     */
    InputStream openInputStream(File file) throws IOException;

    /**
     * Creates a temporary of the specified file and returns it. Once the API will be closed, the temporary file will be dropped.
     */
    File openAsTemp(File file) throws IOException;

    /**
     * Creates an output stream to the specified file.
     */
    OutputStream openOutputStream(File file) throws IOException;

    /**
     * Reverts the specified file.
     */
    GitAPI revert(File file) throws RepositoryException;

    /**
     * Commits all the current changes using the specified message.
     */
    GitAPI commitAll(String message, String username, String email) throws RepositoryException;

    /**
     * Pushes current changes to the remote repository.
     */
    GitAPI push() throws RepositoryException;

    /**
     * Deletes the current repository.
     */
    GitAPI delete() throws RepositoryException;

    /**
     * Returns whether this API access has been closed. In that case, further operation are not allowed.
     */
    boolean isClosed();
}
