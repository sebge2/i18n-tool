package be.sgerard.i18n.service.git;

import be.sgerard.i18n.model.security.user.UserEntity;
import be.sgerard.i18n.service.i18n.file.TranslationFileUtils;
import be.sgerard.i18n.service.security.auth.AuthenticationManager;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static be.sgerard.i18n.service.git.RepositoryManagerImpl.DEFAULT_BRANCH;
import static be.sgerard.i18n.service.i18n.file.TranslationFileUtils.removeParentFile;
import static java.util.stream.Collectors.toList;

/**
 * @author Sebastien Gerard
 */
class DefaultRepositoryAPI implements RepositoryAPI, AutoCloseable {

    public static final Pattern REMOTE_BRANCH_PATTERN = Pattern.compile("^refs\\/remotes\\/\\w+\\/(.+)$");

    public static final Pattern LOCAL_BRANCH_PATTERN = Pattern.compile("^(master)|refs\\/heads\\/(.+)$");

    private static final Logger logger = LoggerFactory.getLogger(DefaultRepositoryAPI.class);

    private final Git git;
    private final File localRepositoryLocation;
    private final AuthenticationManager authenticationManager;
    private final PullRequestManager pullRequestManager;

    private final List<File> modifiedFiles = new ArrayList<>();
    private final File tempDirectory;
    private boolean closed = false;

    DefaultRepositoryAPI(Git git,
                         File localRepositoryLocation,
                         AuthenticationManager authenticationManager,
                         PullRequestManager pullRequestManager) {
        this.git = git;
        this.localRepositoryLocation = localRepositoryLocation;
        this.authenticationManager = authenticationManager;
        this.pullRequestManager = pullRequestManager;

        try {
            this.tempDirectory = Files.createTempDirectory("repo-api-").toFile();
        } catch (IOException e) {
            throw new RuntimeException("Cannot create temporary directory.", e);
        }

        checkout(DEFAULT_BRANCH);
    }

    @Override
    public String getCurrentBranch() throws RepositoryException {
        try {
            return git.getRepository().getBranch();
        } catch (IOException e) {
            throw new RepositoryException("Error while retrieving current directory.", e);
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
    public void checkout(String branch) throws RepositoryException {
        checkNoModifiedFile();

        if (listLocalBranches().contains(branch)) {
            try {
                git
                    .checkout()
                    .setName(branch)
                    .call();
            } catch (Exception e) {
                throw new RepositoryException("Error while checkouting branch [" + branch + "].", e);
            }
        } else if (listRemoteBranches().contains(branch)) {
            try {
                git
                    .checkout()
                    .setCreateBranch(true)
                    .setName(branch)
                    .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
                    .setStartPoint("origin/" + branch)
                    .call();
            } catch (Exception e) {
                throw new RepositoryException("Error while checkouting branch [" + branch + "].", e);
            }
        } else {
            throw new IllegalArgumentException("The branch [" + branch + "] does not exist.");
        }
    }

    @Override
    public void createBranch(String branch) throws RepositoryException {
        checkNoModifiedFile();

        if (listRemoteBranches().contains(branch)) {
            throw new IllegalArgumentException("The branch [" + branch + "] already exists.");
        }

        try {
            git.checkout()
                .setCreateBranch(true)
                .setName(branch)
                .call();
        } catch (GitAPIException e) {
            throw new RepositoryException("Error while creating branch [" + branch + "].", e);
        }
    }

    @Override
    public void removeBranch(String branch) throws RepositoryException {
        if (Objects.equals(branch, DEFAULT_BRANCH)) {
            throw new IllegalArgumentException("Cannot delete branch " + DEFAULT_BRANCH + ".");
        }

        checkNoModifiedFile();

        if (Objects.equals(branch, getCurrentBranch())) {
            checkout(DEFAULT_BRANCH);
        }

        try {
            git.branchDelete().setBranchNames(branch).setForce(true).call();
        } catch (GitAPIException e) {
            throw new RepositoryException("Error while deleting branch [" + branch + "].", e);
        }
    }

    @Override
    public void updateLocalRepository() throws RepositoryException {
        checkNoModifiedFile();

        try {
            git.fetch()
                .setCredentialsProvider(createProvider())
                .setRemoveDeletedRefs(true)
                .call();

            final PullResult result = git.pull()
                .setRemote("origin")
                .setCredentialsProvider(createProvider())
                .call();

            if (!result.isSuccessful()) {
                throw new IllegalStateException("The pull has not been successful.");
            }
        } catch (Exception e) {
            throw new RepositoryException("Error while updating local repository.", e);
        }
    }

    @Override
    public Stream<File> listAllFiles(File file) throws IOException {
        try {
            return TranslationFileUtils.listFiles(getFQNFile(file))
                .map(subFile -> removeParentFile(getLocalRepositoryLocation(), subFile));
        } catch (Exception e) {
            throw new IOException("Error while listing files of [" + file + "].", e);
        }
    }

    @Override
    public Stream<File> listNormalFiles(File file) throws IOException {
        try {
            return TranslationFileUtils.listFiles(getFQNFile(file))
                .filter(File::isFile)
                .map(subFile -> removeParentFile(getLocalRepositoryLocation(), subFile));
        } catch (Exception e) {
            throw new IOException("Error while listing files of [" + file + "].", e);
        }
    }

    @Override
    public Stream<File> listDirectories(File file) throws IOException {
        try {
            return TranslationFileUtils.listFiles(getFQNFile(file))
                .filter(File::isDirectory)
                .map(subFile -> removeParentFile(getLocalRepositoryLocation(), subFile));
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
    public File openAsTemp(File file) {
        try {
            final Path target = getFQNFile(file).toPath();
            final Path link = new File(tempDirectory, file.toString()).toPath();

            Files.createDirectories(link.getParent());

            return Files.createSymbolicLink(link, target).toFile();
        } catch (IOException e) {
            throw new RuntimeException("Cannot open file [" + file + "].", e);
        }
    }

    @Override
    public void revert(File file) throws RepositoryException {
        try {
            git.checkout().addPath(file.toString()).call();
        } catch (Exception e) {
            throw new RepositoryException("Error while reverting.", e);
        } finally {
            modifiedFiles.remove(file);
        }
    }

    @Override
    public void commitAll(String message) throws RepositoryException {
        try {
            git.add().addFilepattern(".").call();

            final UserEntity currentUser = authenticationManager.getCurrentUserOrFail();

            git.commit()
                .setAuthor(currentUser.getUsername(), currentUser.getEmail())
                .setMessage(message)
                .call();

            git.push().setCredentialsProvider(createProvider()).call();
        } catch (Exception e) {
            throw new RepositoryException("Error while committing.", e);
        }
    }

    @Override
    public PullRequestManager getPullRequestManager() {
        return pullRequestManager;
    }

    @Override
    public void close() {
        try {
            try {
                try {
                    FileUtils.forceDelete(tempDirectory);
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

    private File getLocalRepositoryLocation() {
        return localRepositoryLocation;
    }

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

    private List<String> doListBranches(Pattern branchPattern) throws RepositoryException {
        try {
            return git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call().stream()
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
            throw new RepositoryException("Error while listing branches.", e);
        }
    }

    private File getFQNFile(File file) {
        return new File(getLocalRepositoryLocation(), file.toString());
    }

    private UsernamePasswordCredentialsProvider createProvider() {
        return new UsernamePasswordCredentialsProvider(authenticationManager.getGitToken(), "");
    }
}
