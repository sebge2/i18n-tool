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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static be.sgerard.poc.githuboauth.service.git.RepositoryManagerImpl.BRANCHES_TO_KEEP;
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
    public List<String> listBranches() throws RepositoryException {
        try {
            return git.branchList().setListMode(ListBranchCommand.ListMode.REMOTE).call().stream()
                    .map(Ref::getName)
                    .map(name -> name.startsWith(RepositoryManagerImpl.REFS_ORIGIN_PREFIX) ? name.substring(RepositoryManagerImpl.REFS_ORIGIN_PREFIX.length()) : name)
                    .filter(name -> BRANCHES_TO_KEEP.matcher(name).matches())
                    .sorted((first, second) -> {
                        if (RepositoryManagerImpl.DEFAULT_BRANCH.equals(first)) {
                            return -1;
                        } else if (RepositoryManagerImpl.DEFAULT_BRANCH.equals(second)) {
                            return 1;
                        } else {
                            return Comparator.<String>reverseOrder().compare(first, second);
                        }
                    })
                    .distinct()
                    .collect(toList());
        } catch (Exception e) {
            throw new RepositoryException("Error while listing branches.", e);
        }
    }

    @Override
    public void checkout(String branch) throws RepositoryException {
        checkNoModifiedFile();

        if (!listBranches().contains(branch)) {
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

        if (listBranches().contains(branch)) {
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
        checkNoModifiedFile();

        try {
            git.branchDelete().setBranchNames(branch).call();
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
            return TranslationFileUtils.listFiles(new File(getLocalRepositoryLocation(), file.toString()))
                    .map(subFile -> removeParentFile(getLocalRepositoryLocation(), subFile));
        } catch (Exception e) {
            throw new IOException("Error while listing files of [" + file + "].", e);
        }
    }

    @Override
    public Stream<File> listNormalFiles(File file) throws IOException {
        try {
            return TranslationFileUtils.listFiles(new File(getLocalRepositoryLocation(), file.toString()))
                    .filter(File::isFile)
                    .map(subFile -> removeParentFile(getLocalRepositoryLocation(), subFile));
        } catch (Exception e) {
            throw new IOException("Error while listing files of [" + file + "].", e);
        }
    }

    @Override
    public Stream<File> listDirectories(File file) throws IOException {
        try {
            return TranslationFileUtils.listFiles(new File(getLocalRepositoryLocation(), file.toString()))
                    .filter(File::isDirectory)
                    .map(subFile -> removeParentFile(getLocalRepositoryLocation(), subFile));
        } catch (Exception e) {
            throw new IOException("Error while listing directories of [" + file + "].", e);
        }
    }

    @Override
    public InputStream openFile(File file) throws IOException {
        try {
            return new FileInputStream(new File(getLocalRepositoryLocation(), file.toString()));
        } catch (FileNotFoundException e) {
            throw new IOException("Error while opening file [" + file + "].", e);
        }
    }

    @Override
    public OutputStream writeFile(File file) throws IOException {
        try {
            modifiedFiles.add(file);

            return new FileOutputStream(new File(getLocalRepositoryLocation(), file.toString()));
        } catch (FileNotFoundException e) {
            throw new IOException("Error while writing file [" + file + "].", e);
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
            git.add().addFilepattern("*").call();

            final UserEntity currentUser = authenticationManager.getCurrentUserOrFail();

            git.commit()
                    .setAuthor(currentUser.getUsername(), currentUser.getEmail())
                    .setMessage(message)
                    .call();

            git.push().call();
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
            checkNoModifiedFile();
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
}
