package be.sgerard.test.i18n.mock.repository.git;

import be.sgerard.i18n.model.validation.ValidationMessage;
import be.sgerard.i18n.model.validation.ValidationResult;
import be.sgerard.i18n.service.repository.RepositoryException;
import be.sgerard.i18n.service.repository.git.GitRepositoryApi;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;
import java.util.stream.Stream;

import static be.sgerard.i18n.service.repository.git.BaseGitRepositoryApi.INVALID_URL;

/**
 * {@link GitRepositoryApi API} that simply returns an invalid validation result.
 *
 * @author Sebastien Gerard
 */
public class InvalidGitRepositoryApi implements GitRepositoryApi {

    /**
     * Returns the API for accessing an invalid URL.
     */
    public static InvalidGitRepositoryApi createApiInvalidUri(URI uri) {
        return new InvalidGitRepositoryApi(
                ValidationResult.builder()
                        .messages(new ValidationMessage(INVALID_URL, uri))
                        .build()
        );
    }

    private final ValidationResult validationResult;
    private boolean closed;

    public InvalidGitRepositoryApi(ValidationResult validationResult) {
        this.validationResult = validationResult;

        if (validationResult.isSuccessful()) {
            throw new IllegalArgumentException("The validation result must be invalid.");
        }
    }

    @Override
    public GitRepositoryApi init() throws RepositoryException {
        throw new UnsupportedOperationException();
    }

    @Override
    public ValidationResult validateInfo() throws RepositoryException {
        return validationResult;
    }

    @Override
    public GitRepositoryApi pull() throws RepositoryException {
        throw new UnsupportedOperationException();
    }

    @Override
    public GitRepositoryApi fetch() throws RepositoryException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getCurrentBranch() throws RepositoryException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> listRemoteBranches() throws RepositoryException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> listLocalBranches() throws RepositoryException {
        throw new UnsupportedOperationException();
    }

    @Override
    public GitRepositoryApi checkoutDefaultBranch() throws RepositoryException {
        throw new UnsupportedOperationException();
    }

    @Override
    public GitRepositoryApi checkout(String branch) throws RepositoryException {
        throw new UnsupportedOperationException();
    }

    @Override
    public GitRepositoryApi createBranch(String branch) throws RepositoryException {
        throw new UnsupportedOperationException();
    }

    @Override
    public GitRepositoryApi removeBranch(String branch) throws RepositoryException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Stream<File> listAllFiles(File file) throws RepositoryException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Stream<File> listNormalFiles(File file) throws RepositoryException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Stream<File> listDirectories(File file) throws RepositoryException {
        throw new UnsupportedOperationException();
    }

    @Override
    public InputStream openInputStream(File file) throws RepositoryException {
        throw new UnsupportedOperationException();
    }

    @Override
    public File openAsTemp(File file, boolean createIfNotExists) throws RepositoryException {
        throw new UnsupportedOperationException();
    }

    @Override
    public OutputStream openOutputStream(File file, boolean createIfNotExists) throws RepositoryException {
        throw new UnsupportedOperationException();
    }

    @Override
    public GitRepositoryApi revert(File file) throws RepositoryException {
        throw new UnsupportedOperationException();
    }

    @Override
    public GitRepositoryApi removeFile(File file) throws RepositoryException {
        throw new UnsupportedOperationException();
    }

    @Override
    public GitRepositoryApi commitAll(String message) throws RepositoryException {
        throw new UnsupportedOperationException();
    }

    @Override
    public GitRepositoryApi push() throws RepositoryException {
        throw new UnsupportedOperationException();
    }

    @Override
    public GitRepositoryApi resetHardHead() throws RepositoryException {
        throw new UnsupportedOperationException();
    }

    @Override
    public GitRepositoryApi delete() throws RepositoryException {
        throw new UnsupportedOperationException();
    }

    @Override
    public GitRepositoryApi merge(String branch) throws RepositoryException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public void close() {
        this.closed = true;
    }
}
