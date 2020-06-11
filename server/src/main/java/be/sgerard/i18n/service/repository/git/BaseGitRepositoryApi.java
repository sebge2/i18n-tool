package be.sgerard.i18n.service.repository.git;

import be.sgerard.i18n.service.i18n.file.TranslationFileUtils;
import be.sgerard.i18n.service.repository.RepositoryException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static be.sgerard.i18n.service.i18n.file.TranslationFileUtils.removeParentFile;

/**
 * Base implementation of the {@link GitRepositoryApi Git API}.
 *
 * @author Sebastien Gerard
 */
public abstract class BaseGitRepositoryApi implements GitRepositoryApi {

    /**
     * Pattern of a remote branch.
     */
    public static final Pattern REMOTE_BRANCH_PATTERN = Pattern.compile("^refs/remotes/\\w+/(.+)$");

    /**
     * Pattern of a local branch.
     */
    public static final Pattern LOCAL_BRANCH_PATTERN = Pattern.compile("^(master)|refs/heads/(.+)$");

    /**
     * Validation message key specifying that Git credentials are invalid.
     */
    public static final String INVALID_CREDENTIALS = "validation.git.invalid-credentials";

    /**
     * Validation message key specifying that the Git URL are invalid.
     */
    public static final String INVALID_URL = "validation.git.invalid-url";

    /**
     * Validation message key specifying that there was an error accessing the repository;
     */
    public static final String ERROR_ACCESSING = "validation.git.error-accessing";

    private static final Logger logger = LoggerFactory.getLogger(BaseGitRepositoryApi.class);

    protected final URI remoteUri;
    protected final File repositoryLocation;
    private final List<File> modifiedFiles = new ArrayList<>();
    private final File tempDirectory;
    private boolean closed = false;

    public BaseGitRepositoryApi(Configuration configuration) {
        this.remoteUri = configuration.getRemoteUri();
        this.repositoryLocation = configuration.getRepositoryLocation();

        try {
            this.tempDirectory = Files.createTempDirectory("repo-api-").toFile();
        } catch (IOException e) {
            throw new RuntimeException("Cannot create temporary directory.", e);
        }
    }

    @Override
    public GitRepositoryApi init() throws RepositoryException {
        try {
            if (repositoryLocation.exists()) {
                FileUtils.cleanDirectory(repositoryLocation);
            }

            doInit();

            return this;
        } catch (Exception e) {
            throw RepositoryException.onOpen(e);
        }
    }

    @Override
    public GitRepositoryApi createBranch(String branch) throws RepositoryException {
        checkNoModifiedFile();

        if (listRemoteBranches().contains(branch) || listLocalBranches().contains(branch)) {
            throw RepositoryException.onBranchAlreadyExist(branch);
        }

        try {
            doCreateBranch(branch);

            return this;
        } catch (Exception e) {
            throw RepositoryException.onBranchCreation(branch, e);
        }
    }

    @Override
    public GitRepositoryApi removeBranch(String branch) throws RepositoryException {
        if (Objects.equals(branch, DEFAULT_BRANCH)) {
            throw RepositoryException.onForbiddenBranchDeletion(branch);
        }

        checkNoModifiedFile();

        if (Objects.equals(branch, getCurrentBranch())) {
            checkout(DEFAULT_BRANCH);
        }

        try {
            doRemoveBranch(branch);

            return this;
        } catch (Exception e) {
            throw RepositoryException.onBranchDeletion(branch, e);
        }
    }

    @Override
    public Stream<File> listAllFiles(File file) throws RepositoryException {
        try {
            return TranslationFileUtils.listFiles(getFQNFile(file))
                    .map(subFile -> removeParentFile(repositoryLocation, subFile));
        } catch (Exception e) {
            throw RepositoryException.onFileListing(file, e);
        }
    }

    @Override
    public Stream<File> listNormalFiles(File file) throws RepositoryException {
        try {
            return TranslationFileUtils.listFiles(getFQNFile(file))
                    .filter(File::isFile)
                    .map(subFile -> removeParentFile(repositoryLocation, subFile));
        } catch (Exception e) {
            throw RepositoryException.onFileListing(file, e);
        }
    }

    @Override
    public Stream<File> listDirectories(File file) throws RepositoryException {
        try {
            return TranslationFileUtils.listFiles(getFQNFile(file))
                    .filter(File::isDirectory)
                    .map(subFile -> removeParentFile(repositoryLocation, subFile));
        } catch (Exception e) {
            throw RepositoryException.onFileListing(file, e);
        }
    }

    @Override
    public InputStream openInputStream(File file) throws RepositoryException {
        try {
            return new FileInputStream(getFQNFile(file));
        } catch (FileNotFoundException e) {
            throw RepositoryException.onFileReading(file, e);
        }
    }

    @Override
    public OutputStream openOutputStream(File file) throws RepositoryException {
        try {
            addModifiedFile(file);

            return new FileOutputStream(getFQNFile(file));
        } catch (FileNotFoundException e) {
            throw RepositoryException.onFileWriting(file, e);
        }
    }

    @Override
    public File openAsTemp(File file) throws RepositoryException {
        try {
            final Path target = getFQNFile(file).toPath();
            final Path link = new File(tempDirectory, file.toString()).toPath();

            Files.createDirectories(link.getParent());

            return Files.createSymbolicLink(link, target).toFile();
        } catch (IOException e) {
            throw RepositoryException.onFileWriting(file, e);
        }
    }

    @Override
    public GitRepositoryApi delete() throws RepositoryException {
        try {
            if (repositoryLocation.exists()) {
                FileUtils.deleteDirectory(repositoryLocation);
            }

            return this;
        } catch (Exception e) {
            throw RepositoryException.onDelete(e);
        }
    }

    @Override
    public void close() {
        try {
            try {
                try {
                    if (tempDirectory.exists()) {
                        FileUtils.forceDelete(tempDirectory);
                    }
                } catch (IOException e) {
                    logger.warn("Cannot delete temporary directory [" + tempDirectory + "].");
                }

                checkNoModifiedFile();
            } finally {
                checkout(DEFAULT_BRANCH);
            }
        } finally {
            this.closed = true;
        }
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    /**
     * Initializes the local repository, the directory is created.
     */
    protected abstract void doInit() throws Exception;

    /**
     * Creates the specified branch, all checks have been performed.
     */
    protected abstract void doCreateBranch(String branch) throws Exception;

    /**
     * Removes the specified branch, all checks have been performed.
     */
    protected abstract void doRemoveBranch(String branch) throws Exception;

    /**
     * Checks that no file has been modified and not committed.
     */
    protected void checkNoModifiedFile() {
        if (!modifiedFiles.isEmpty()) {
            final ArrayList<File> files = new ArrayList<>(modifiedFiles);

            for (File modifiedFile : files) {
                try {
                    revert(modifiedFile);
                } catch (RepositoryException e) {
                    logger.error("Error while reverting.", e);
                }
            }

            throw new IllegalStateException("There are modified files that have not been committed: " + files + ".");
        }
    }

    /**
     * Returns the fully-qualified file based on the specified relative file.
     */
    protected File getFQNFile(File file) {
        return new File(repositoryLocation, file.toString());
    }

    /**
     * Adds the specified file to the list of files that have been modified.
     */
    protected void addModifiedFile(File file) {
        modifiedFiles.add(file);
    }

    /**
     * Removes the specified file from the list of files that have been modified.
     */
    protected void removeModifiedFile(File file) {
        modifiedFiles.remove(file);
    }

    /**
     * Clears all the modified files.
     */
    protected void clearModifiedFiles() {
        modifiedFiles.clear();
    }
}
