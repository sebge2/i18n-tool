package be.sgerard.poc.githuboauth.service.git;

import be.sgerard.poc.githuboauth.model.security.user.UserEntity;
import be.sgerard.poc.githuboauth.service.i18n.file.TranslationFileUtils;
import be.sgerard.poc.githuboauth.service.security.auth.AuthenticationManager;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static be.sgerard.poc.githuboauth.service.git.RepositoryManagerImpl.*;
import static be.sgerard.poc.githuboauth.service.i18n.file.TranslationFileUtils.removeParentFile;
import static java.util.stream.Collectors.toList;

/**
 * @author Sebastien Gerard
 */
class DefaultRepositoryAPI implements RepositoryAPI, AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(DefaultRepositoryAPI.class);

    private final Git git;
    private final File localRepositoryLocation;
    private final UsernamePasswordCredentialsProvider credentialsProvider;
    private final AuthenticationManager authenticationManager;
    private final PullRequestManager pullRequestManager;

    private final List<File> modifiedFiles = new ArrayList<>();
    private boolean closed = false;

    DefaultRepositoryAPI(Git git,
                         File localRepositoryLocation,
                         UsernamePasswordCredentialsProvider credentialsProvider,
                         AuthenticationManager authenticationManager,
                         PullRequestManager pullRequestManager) {
        this.git = git;
        this.localRepositoryLocation = localRepositoryLocation;
        this.credentialsProvider = credentialsProvider;
        this.authenticationManager = authenticationManager;
        this.pullRequestManager = pullRequestManager;

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
        return doListBranches(REFS_ORIGIN_PREFIX);
    }

    @Override
    public List<String> listLocalBranches() throws RepositoryException {
        return doListBranches(REFS_LOCAL_PREFIX);
    }

    @Override
    public void checkout(String branch) throws RepositoryException {
        checkNoModifiedFile();

        if (!listLocalBranches().contains(branch)) {
            throw new IllegalArgumentException("The branch [" + branch + "] does not exist.");
        }

        try {
            git.checkout().setName(branch).call();
        } catch (Exception e) {
            throw new RepositoryException("Error while checkouting branch [" + branch + "].", e);
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

        if(Objects.equals(branch, getCurrentBranch())){
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
            git.pull().setCredentialsProvider(credentialsProvider).call();
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
    public InputStream openFile(File file) throws IOException {
        try {
            return new FileInputStream(getFQNFile(file));
        } catch (FileNotFoundException e) {
            throw new IOException("Error while opening file [" + file + "].", e);
        }
    }

    @Override
    public OutputStream writeFile(File file) throws IOException {
        try {
            modifiedFiles.add(file);

            return new FileOutputStream(getFQNFile(file));
        } catch (FileNotFoundException e) {
            throw new IOException("Error while writing file [" + file + "].", e);
        }
    }

    @Override
    public File getFQNFile(File file) {
        // TODO create a temporary
        return new File(getLocalRepositoryLocation(), file.toString());
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

            git.push().setCredentialsProvider(credentialsProvider).call();
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

    private List<String> doListBranches(String branchPrefix) throws RepositoryException {
        try {
            return git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call().stream()
                    .map(Ref::getName)
                    .map(name -> name.startsWith(branchPrefix) ? name.substring(branchPrefix.length()) : name)
                    .distinct()
                    .collect(toList());
        } catch (Exception e) {
            throw new RepositoryException("Error while listing branches.", e);
        }
    }
}
