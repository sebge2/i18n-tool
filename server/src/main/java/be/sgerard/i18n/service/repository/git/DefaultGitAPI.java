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
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.proxy.Proxy;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static be.sgerard.i18n.service.i18n.file.TranslationFileUtils.removeParentFile;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

/**
 * Default implementation of the {@link GitAPI Git API}.
 *
 * @author Sebastien Gerard
 */
public class DefaultGitAPI implements GitAPI, AutoCloseable {

    /**
     * Pattern of a remote branch.
     */
    public static final Pattern REMOTE_BRANCH_PATTERN = Pattern.compile("^refs\\/remotes\\/\\w+\\/(.+)$");

    /**
     * Pattern of a local branch.
     */
    public static final Pattern LOCAL_BRANCH_PATTERN = Pattern.compile("^(master)|refs\\/heads\\/(.+)$");

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
     * Creates a new {@link GitAPI API object} using the specified {@link Configuration configuration}.
     */
    public static GitAPI createAPI(Configuration configuration) {
        final DefaultGitAPI delegate = new DefaultGitAPI(configuration);

        return (GitAPI) Proxy.newProxyInstance(
                GitAPI.class.getClassLoader(),
                new Class<?>[]{GitAPI.class},
                (o, method, objects) -> {
                    if (!"close".equals(method.getName()) && delegate.isClosed()) {
                        throw new IllegalStateException("Cannot access the API once closed.");
                    }

                    try {
                        final Object result = method.invoke(delegate, objects);

                        if (result == delegate) {
                            return o;
                        }

                        return result;
                    } catch (InvocationTargetException e) {
                        throw e.getCause();
                    }
                }
        );
    }

    private static final Logger logger = LoggerFactory.getLogger(DefaultGitAPI.class);

    private final URI remoteUri;
    private final File repositoryLocation;
    private final CredentialsProvider credentialsProvider;
    private final String defaultBranch;

    private Git git;
    private final List<File> modifiedFiles = new ArrayList<>();
    private final File tempDirectory;
    private boolean closed = false;

    public DefaultGitAPI(Configuration configuration) {
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
    public GitAPI init() throws RepositoryException {
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
    public GitAPI validateInfo() throws RepositoryException, ValidationException {
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
    public GitAPI checkout(String branch) throws RepositoryException {
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
    public GitAPI createBranch(String branch) throws RepositoryException {
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
    public GitAPI removeBranch(String branch) throws RepositoryException {
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
    public GitAPI update() throws RepositoryException {
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
    public Stream<File> listAllFiles(File file) throws IOException {
        try {
            return TranslationFileUtils.listFiles(getFQNFile(file))
                    .map(subFile -> removeParentFile(repositoryLocation, subFile));
        } catch (Exception e) {
            throw new IOException("Error while listing files of [" + file + "].", e);
        }
    }

    @Override
    public Stream<File> listNormalFiles(File file) throws IOException {
        try {
            return TranslationFileUtils.listFiles(getFQNFile(file))
                    .filter(File::isFile)
                    .map(subFile -> removeParentFile(repositoryLocation, subFile));
        } catch (Exception e) {
            throw new IOException("Error while listing files of [" + file + "].", e);
        }
    }

    @Override
    public Stream<File> listDirectories(File file) throws IOException {
        try {
            return TranslationFileUtils.listFiles(getFQNFile(file))
                    .filter(File::isDirectory)
                    .map(subFile -> removeParentFile(repositoryLocation, subFile));
        } catch (Exception e) {
            throw new IOException("Error while listing directories of [" + file + "].", e);
        }
    }

    @Override
    public InputStream openInputStream(File file) throws IOException {
        try {
            return new FileInputStream(getFQNFile(file));
        } catch (FileNotFoundException e) {
            throw new IOException("Error while opening file [" + file + "].", e);
        }
    }

    @Override
    public OutputStream openOutputStream(File file) throws IOException {
        try {
            modifiedFiles.add(file);

            return new FileOutputStream(getFQNFile(file));
        } catch (FileNotFoundException e) {
            throw new IOException("Error while writing file [" + file + "].", e);
        }
    }

    @Override
    public File openAsTemp(File file) throws IOException {
        try {
            final Path target = getFQNFile(file).toPath();
            final Path link = new File(tempDirectory, file.toString()).toPath();

            Files.createDirectories(link.getParent());

            return Files.createSymbolicLink(link, target).toFile();
        } catch (IOException e) {
            throw new IOException("Cannot open file [" + file + "].", e);
        }
    }

    @Override
    public GitAPI revert(File file) throws RepositoryException {
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
    public GitAPI commitAll(String message, String username, String email) throws RepositoryException {
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
    public GitAPI push() throws RepositoryException {
        try {
            openGit().push().setCredentialsProvider(credentialsProvider).call();
            return this;
        } catch (Exception e) {
            throw RepositoryException.onPush(e);
        }
    }

    @Override
    public GitAPI delete() throws RepositoryException {
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

    /**
     * Git configuration.
     */
    public static final class Configuration {

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
