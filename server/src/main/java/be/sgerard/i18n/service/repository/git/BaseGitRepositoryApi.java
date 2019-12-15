package be.sgerard.i18n.service.repository.git;

import be.sgerard.i18n.service.i18n.file.TranslationFileUtils;
import be.sgerard.i18n.service.repository.RepositoryException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
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

    protected final Configuration configuration;

    private File tempDirectory;
    private boolean closed = false;

    public BaseGitRepositoryApi(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public GitRepositoryApi init() throws RepositoryException {
        try {
            if (configuration.getRepositoryLocation().exists()) {
                FileUtils.cleanDirectory(configuration.getRepositoryLocation());
            }

            doInit();

            return this;
        } catch (Exception e) {
            throw RepositoryException.onOpen(e);
        }
    }

    @Override
    public GitRepositoryApi checkoutDefaultBranch() throws RepositoryException {
        return checkout(configuration.getDefaultBranch());
    }

    @Override
    public GitRepositoryApi createBranch(String branch) throws RepositoryException {
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
                    .map(subFile -> removeParentFile(configuration.getRepositoryLocation(), subFile));
        } catch (Exception e) {
            throw RepositoryException.onFileListing(file, e);
        }
    }

    @Override
    public Stream<File> listNormalFiles(File file) throws RepositoryException {
        try {
            return TranslationFileUtils.listFiles(getFQNFile(file))
                    .filter(File::isFile)
                    .map(subFile -> removeParentFile(configuration.getRepositoryLocation(), subFile));
        } catch (Exception e) {
            throw RepositoryException.onFileListing(file, e);
        }
    }

    @Override
    public Stream<File> listDirectories(File file) throws RepositoryException {
        try {
            return TranslationFileUtils.listFiles(getFQNFile(file))
                    .filter(File::isDirectory)
                    .map(subFile -> removeParentFile(configuration.getRepositoryLocation(), subFile));
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
    public OutputStream openOutputStream(File file, boolean createIfNotExists) throws RepositoryException {
        try {
            final File fqnFile = getFQNFile(file);

            if (!fqnFile.exists() && createIfNotExists) {
                Files.createFile(fqnFile.toPath());
            }

            return new FileOutputStream(fqnFile);
        } catch (Exception e) {
            throw RepositoryException.onFileWriting(file, e);
        }
    }

    @Override
    public File openAsTemp(File file, boolean createIfNotExists) throws RepositoryException {
        try {
            final File target = getFQNFile(file);

            if (!target.exists() && createIfNotExists) {
                Files.createFile(target.toPath());
            }

            final Path link = new File(getOrCreateTemporaryDirectory(), file.toString()).toPath();

            Files.createDirectories(link.getParent());

            return Files.createSymbolicLink(link, target.toPath()).toFile();
        } catch (IOException e) {
            throw RepositoryException.onFileWriting(file, e);
        }
    }

    @Override
    public GitRepositoryApi delete() throws RepositoryException {
        try {
            if (configuration.getRepositoryLocation().exists()) {
                FileUtils.deleteDirectory(configuration.getRepositoryLocation());
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
                    if ((tempDirectory != null) && tempDirectory.exists()) {
                        FileUtils.forceDelete(tempDirectory);
                    }
                } catch (IOException e) {
                    logger.warn("Cannot delete temporary directory [" + tempDirectory + "].");
                }
            } finally {
                if (configuration.getRepositoryLocation().exists()) {
                    try {
                        checkout(DEFAULT_BRANCH);
                    } catch (RepositoryException e) {
                        logger.debug("Error while doing a checkout of the default branch. " +
                                "The repository may not have been initialized properly.", e);
                    }
                }
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
     * Returns the fully-qualified file based on the specified relative file.
     */
    protected File getFQNFile(File file) {
        return new File(configuration.getRepositoryLocation(), file.toString());
    }

    /**
     * Returns the temporary directory, if it does not exist, a new fresh one is created.
     */
    private File getOrCreateTemporaryDirectory() {
        if (tempDirectory == null) {
            try {
                this.tempDirectory = Files.createTempDirectory("repo-api-").toFile();
            } catch (IOException e) {
                throw new RuntimeException("Cannot create temporary directory.", e);
            }
        }

        return this.tempDirectory;
    }
}
