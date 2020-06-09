package be.sgerard.i18n.service.repository.git;

import be.sgerard.i18n.model.validation.ValidationMessage;
import be.sgerard.i18n.model.validation.ValidationResult;
import be.sgerard.i18n.service.ValidationException;
import be.sgerard.i18n.service.i18n.file.TranslationFileUtils;
import be.sgerard.i18n.service.repository.RepositoryException;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static be.sgerard.i18n.service.i18n.file.TranslationFileUtils.removeParentFile;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

/**
 * Default implementation of the {@link GitRepositoryApi Git API}.
 *
 * @author Sebastien Gerard
 */
public class DefaultGitRepositoryApi implements GitRepositoryApi {

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

    /**
     * Creates a new {@link GitRepositoryApi API object} using the specified {@link Configuration configuration}.
     */
    public static GitRepositoryApi createAPI(Configuration configuration) {
        return new DefaultGitRepositoryApi(configuration);
    }

    private static final Logger logger = LoggerFactory.getLogger(DefaultGitRepositoryApi.class);

    private final URI remoteUri;
    private final File repositoryLocation;
    private final CredentialsProvider credentialsProvider;
    private final String defaultBranch;

    private Git git;
    private final List<File> modifiedFiles = new ArrayList<>();
    private final File tempDirectory;
    private boolean closed = false;

    public DefaultGitRepositoryApi(Configuration configuration) {
        this.remoteUri = configuration.getRemoteUri();
        this.repositoryLocation = configuration.getRepositoryLocation();
        this.credentialsProvider = configuration.toCredentialsProvider().orElse(null);
        this.defaultBranch = configuration.getDefaultBranch();

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

            Git.cloneRepository()
                    .setCredentialsProvider(credentialsProvider)
                    .setURI(remoteUri.toString())
                    .setDirectory(repositoryLocation)
                    .setBranchesToClone(singletonList(defaultBranch))
                    .setBranch(defaultBranch)
                    .call();

            checkout(defaultBranch);

            return this;
        } catch (Exception e) {
            throw RepositoryException.onOpen(e);
        }
    }

    @Override
    public GitRepositoryApi validateInfo() throws RepositoryException, ValidationException {
        try {
            Git
                    .wrap(FileRepositoryBuilder.create(FileUtils.getTempDirectory()))
                    .lsRemote()
                    .setCredentialsProvider(credentialsProvider)
                    .setRemote(remoteUri.toString())
                    .call();

            return this;
        } catch (IOException e) {
            throw RepositoryException.onOpen(e);
        } catch (GitAPIException e) {
            final String errorMessage = e.getMessage().toLowerCase();
            if (errorMessage.contains("not authorized") || errorMessage.contains("authentication is required")) {
                throw new ValidationException(
                        ValidationResult.builder()
                                .messages(new ValidationMessage(INVALID_CREDENTIALS, remoteUri))
                                .build()
                );
            } else if (errorMessage.contains("not found")) {
                throw new ValidationException(
                        ValidationResult.builder()
                                .messages(new ValidationMessage(INVALID_URL, remoteUri))
                                .build()
                );
            } else {
                throw new ValidationException(
                        ValidationResult.builder()
                                .messages(new ValidationMessage(ERROR_ACCESSING, remoteUri))
                                .build()
                );
            }
        }
    }

    @Override
    public String getCurrentBranch() throws RepositoryException {
        try {
            return openGit().getRepository().getBranch();
        } catch (Exception e) {
            throw RepositoryException.onBranchListing(e);
        }
    }

    @Override
    public List<String> listRemoteBranches() throws RepositoryException {
        return doListBranches(REMOTE_BRANCH_PATTERN);
    }

    @Override
    public List<String> listLocalBranches() throws RepositoryException {
        return doListBranches(LOCAL_BRANCH_PATTERN);
    }

    @Override
    public GitRepositoryApi checkout(String branch) throws RepositoryException {
        checkNoModifiedFile();

        if (listLocalBranches().contains(branch)) {
            try {
                openGit()
                        .checkout()
                        .setName(branch)
                        .call();

                return this;
            } catch (Exception e) {
                throw RepositoryException.onBranchSwitching(branch, e);
            }
        } else if (listRemoteBranches().contains(branch)) {
            try {
                openGit()
                        .checkout()
                        .setCreateBranch(true)
                        .setName(branch)
                        .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
                        .setStartPoint("origin/" + branch)
                        .call();

                return this;
            } catch (Exception e) {
                throw RepositoryException.onBranchSwitching(branch, e);
            }
        } else {
            throw RepositoryException.onBranchNotFound(branch);
        }
    }

    @Override
    public GitRepositoryApi createBranch(String branch) throws RepositoryException {
        checkNoModifiedFile();

        if (listRemoteBranches().contains(branch) || listLocalBranches().contains(branch)) {
            throw RepositoryException.onBranchAlreadyExist(branch);
        }

        try {
            openGit().checkout()
                    .setCreateBranch(true)
                    .setName(branch)
                    .call();

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
            openGit().branchDelete().setBranchNames(branch).setForce(true).call();

            return this;
        } catch (Exception e) {
            throw RepositoryException.onBranchDeletion(branch, e);
        }
    }

    @Override
    public GitRepositoryApi pull() throws RepositoryException {
        checkNoModifiedFile();

        try {
            openGit().fetch()
                    .setCredentialsProvider(credentialsProvider)
                    .setRemoveDeletedRefs(true)
                    .call();

            final PullResult result = openGit().pull()
                    .setRemote("origin")
                    .setCredentialsProvider(credentialsProvider)
                    .call();

            if (!result.isSuccessful()) {
                throw RepositoryException.onUpdate(null);
            }

            return this;
        } catch (Exception e) {
            throw RepositoryException.onUpdate(e);
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
            modifiedFiles.add(file);

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
    public GitRepositoryApi revert(File file) throws RepositoryException {
        try {
            openGit().checkout().addPath(file.toString()).call();

            return this;
        } catch (Exception e) {
            throw RepositoryException.onRevert(e);
        } finally {
            modifiedFiles.remove(file);
        }
    }

    @Override
    public GitRepositoryApi commitAll(String message, String username, String email) throws RepositoryException {
        try {
            openGit().add().addFilepattern(".").call();

            openGit().commit()
                    .setAuthor(username, email)
                    .setMessage(message)
                    .call();

            return this;
        } catch (Exception e) {
            throw RepositoryException.onPush(e);
        }
    }

    @Override
    public GitRepositoryApi push() throws RepositoryException {
        try {
            openGit().push().setCredentialsProvider(credentialsProvider).call();
            return this;
        } catch (Exception e) {
            throw RepositoryException.onPush(e);
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
     * Checks that no file has been modified and not committed.
     */
    private void checkNoModifiedFile() {
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
     * Lists branches having the specified pattern.
     */
    private List<String> doListBranches(Pattern branchPattern) throws RepositoryException {
        try {
            return openGit().branchList().setListMode(ListBranchCommand.ListMode.ALL).call().stream()
                    .map(Ref::getName)
                    .map(name -> {
                        final Matcher matcher = branchPattern.matcher(name);

                        if (!matcher.matches()) {
                            return null;
                        } else if (matcher.groupCount() == 2) {
                            return matcher.group(2);
                        } else {
                            return matcher.group(1);
                        }
                    })
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(toList());
        } catch (Exception e) {
            throw RepositoryException.onBranchListing(e);
        }
    }

    /**
     * Returns the fully-qualified file based on the specified relative file.
     */
    private File getFQNFile(File file) {
        return new File(repositoryLocation, file.toString());
    }

    /**
     * Opens {@link Git}, or initialize it if needed.
     */
    private Git openGit() throws IOException {
        if (git == null) {
            git = Git.open(repositoryLocation);
        }

        return git;
    }

}
