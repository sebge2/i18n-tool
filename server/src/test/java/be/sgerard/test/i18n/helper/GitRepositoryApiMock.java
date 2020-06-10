package be.sgerard.test.i18n.helper;

import be.sgerard.i18n.model.validation.ValidationMessage;
import be.sgerard.i18n.model.validation.ValidationResult;
import be.sgerard.i18n.service.ValidationException;
import be.sgerard.i18n.service.repository.RepositoryException;
import be.sgerard.i18n.service.repository.git.BaseGitRepositoryApi;
import be.sgerard.i18n.service.repository.git.GitRepositoryApi;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

/**
 * {@link GitRepositoryApi Git Repository API} using a {@link GitRepositoryMock mock Git repository}.
 * The local repository is structured as follow:
 * <ul>
 *     <li>a file containing the remote branches,</li>
 *     <li>/remote</li>
 *     <ul>
 *         <li>a directory per branch.</li>
 *     </ul>
 *     <li>/local</li>
 *     <ul>
 *          <li>a directory per branch.</li>
 *     </ul>
 * </ul>
 *
 * @author Sebastien Gerard
 */
public class GitRepositoryApiMock extends BaseGitRepositoryApi {

    public static final String REMOTE_BRANCHES_FILE = "remote-branches.txt";

    public static final String REMOTE_DIRECTORY = "remote";

    public static final String LOCAL_DIRECTORY = "local";

    private final GitRepositoryApi.Configuration configuration;
    private final GitRepositoryMock repository;

    private String currentBranch = DEFAULT_BRANCH;

    public GitRepositoryApiMock(GitRepositoryMock repository, Configuration configuration) {
        super(configuration);

        this.configuration = configuration;
        this.repository = repository;
    }

    @Override
    public GitRepositoryApi validateInfo() throws RepositoryException, ValidationException {
        if (repository == null) {
            throw new ValidationException(
                    ValidationResult.builder()
                            .messages(new ValidationMessage(INVALID_URL, configuration.getRemoteUri()))
                            .build()
            );
        }

        if (!repository.authenticate(configuration.getUsername().orElse(null), configuration.getPassword().orElse(null))) {
            throw new ValidationException(
                    ValidationResult.builder()
                            .messages(new ValidationMessage(INVALID_CREDENTIALS, configuration.getRemoteUri()))
                            .build()
            );
        }

        return this;
    }

    @Override
    public GitRepositoryApi pull() throws RepositoryException {
        checkNoModifiedFile();

        try {
            final List<String> branches = repository.listBranches();

            FileUtils.writeLines(getRemoteBranchesFile(), branches, false);

            for (String branch : branches) {
                final File remoteBranchDir = getRemoteBranchDir(branch);

                cleanDirectoryIfExists(remoteBranchDir);
                repository.copyTo(branch, remoteBranchDir);
            }

            copyFromTo(getLocalBranchDir(getCurrentBranch()), getRemoteBranchDir(getCurrentBranch()));

            return this;
        } catch (Exception e) {
            throw RepositoryException.onUpdate(e);
        }
    }

    @Override
    public String getCurrentBranch() throws RepositoryException {
        return currentBranch;
    }

    @Override
    public List<String> listRemoteBranches() throws RepositoryException {
        final File remoteBranchesFile = getRemoteBranchesFile();

        if (!remoteBranchesFile.exists()) {
            return emptyList();
        }

        try {
            return Files.readAllLines(remoteBranchesFile.toPath(), Charset.defaultCharset());
        } catch (IOException e) {
            throw RepositoryException.onBranchListing(e);
        }
    }

    @Override
    public List<String> listLocalBranches() throws RepositoryException {
        return Optional.ofNullable(configuration.getRepositoryLocation().list())
                .map(Arrays::asList)
                .orElse(emptyList())
                .stream()
                .filter(file -> new File(configuration.getRepositoryLocation(), file).isDirectory())
                .collect(toList());
    }

    @Override
    public GitRepositoryApi checkout(String branch) throws RepositoryException {
        checkNoModifiedFile();

        currentBranch = branch;

        if (listLocalBranches().contains(branch)) {
            return this;
        } else if (listRemoteBranches().contains(branch)) {
            try {
                final File localBranchDir = getLocalBranchDir(getCurrentBranch());
                final File remoteBranchDir = getRemoteBranchDir(getCurrentBranch());

                cleanDirectoryIfExists(localBranchDir);
                copyFromTo(localBranchDir, remoteBranchDir);

                return this;
            } catch (Exception e) {
                throw RepositoryException.onBranchSwitching(branch, e);
            }
        } else {
            throw RepositoryException.onBranchNotFound(branch);
        }
    }

    @Override
    public GitRepositoryApi revert(File file) throws RepositoryException {
        try {
            FileUtils.copyFile(new File(getRemoteBranchDir(getCurrentBranch()), file.toString()), new File(getLocalBranchDir(getCurrentBranch()), file.toString()));

            return this;
        } catch (Exception e) {
            throw RepositoryException.onRevert(e);
        } finally {
            removeModifiedFile(file);
        }
    }

    @Override
    public GitRepositoryApi commitAll(String message) throws RepositoryException {
        return this;
    }

    @Override
    public GitRepositoryApi push() throws RepositoryException {
        try {
            cleanDirectoryIfExists(getRemoteBranchDir(getCurrentBranch()));

            copyFromTo(getRemoteBranchDir(getCurrentBranch()), getLocalBranchDir(getCurrentBranch()));

            repository.copyFrom(getCurrentBranch(), getLocalBranchDir(getCurrentBranch()));
            return this;
        } catch (Exception e) {
            throw RepositoryException.onPush(e);
        }
    }

    @Override
    protected void doInit() {
        pull();
    }

    @Override
    protected void doCreateBranch(String branch) throws Exception {
        copyFromTo(getLocalBranchDir(branch), getRemoteBranchDir(getCurrentBranch()));
    }

    @Override
    protected void doRemoveBranch(String branch) throws Exception {
        FileUtils.deleteDirectory(getLocalBranchDir(branch));
    }

    private File getLocalBranchDir(String branch) {
        return new File(new File(configuration.getRepositoryLocation(), LOCAL_DIRECTORY), branch);
    }

    private File getRemoteBranchDir(String branch) {
        return new File(new File(configuration.getRepositoryLocation(), REMOTE_DIRECTORY), branch);
    }

    private File getRemoteBranchesFile() {
        return new File(configuration.getRepositoryLocation(), REMOTE_BRANCHES_FILE);
    }

    private void cleanDirectoryIfExists(File remoteBranchDir) throws IOException {
        if (remoteBranchDir.exists()) {
            FileUtils.cleanDirectory(remoteBranchDir);
        }
    }

    private void copyFromTo(File from, File to) throws IOException {
        FileUtils.copyDirectory(to, from);
    }
}
