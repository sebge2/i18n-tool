package be.sgerard.i18n.service.repository.git;

import be.sgerard.i18n.model.validation.ValidationMessage;
import be.sgerard.i18n.model.validation.ValidationResult;
import be.sgerard.i18n.service.ValidationException;
import be.sgerard.i18n.service.repository.RepositoryException;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.BranchConfig;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

/**
 * Default implementation of the {@link GitRepositoryApi Git API}.
 *
 * @author Sebastien Gerard
 */
public class DefaultGitRepositoryApi extends BaseGitRepositoryApi {

    /**
     * Number of attempts for removing the lock file.
     */
    private static final int NUMBER_ATTEMPT_REMOVE_LOCK = 50;

    /**
     * Interval in ms between attempts for removing the lock file.
     */
    private static final int INTERVAL_BETWEEN_ATTEMPTS_REMOVE_LOCK_IN_MS = 100;

    private static final Logger logger = LoggerFactory.getLogger(DefaultGitRepositoryApi.class);

    /**
     * Creates a new {@link GitRepositoryApi API object} using the specified {@link Configuration configuration}.
     */
    public static GitRepositoryApi createAPI(Configuration configuration) {
        return new DefaultGitRepositoryApi(configuration);
    }

    private Git git;

    public DefaultGitRepositoryApi(Configuration configuration) {
        super(configuration);
    }

    @Override
    public GitRepositoryApi validateInfo() throws RepositoryException, ValidationException {
        try {
            Git
                    .wrap(FileRepositoryBuilder.create(FileUtils.getTempDirectory()))
                    .lsRemote()
                    .setCredentialsProvider(configuration.toCredentialsProvider().orElse(null))
                    .setRemote(Objects.toString(configuration.getRemoteUri().orElse(null), null))
                    .call();

            return this;
        } catch (IOException e) {
            throw RepositoryException.onOpen(e);
        } catch (GitAPIException e) {
            final String errorMessage = e.getMessage().toLowerCase();
            if (errorMessage.contains("not authorized") || errorMessage.contains("authentication is required")) {
                throw new ValidationException(
                        ValidationResult.builder()
                                .messages(new ValidationMessage(INVALID_CREDENTIALS, configuration.getRemoteUri().orElse(null)))
                                .build()
                );
            } else if (errorMessage.contains("not found")) {
                throw new ValidationException(
                        ValidationResult.builder()
                                .messages(new ValidationMessage(INVALID_URL, configuration.getRemoteUri().orElse(null)))
                                .build()
                );
            } else {
                throw new ValidationException(
                        ValidationResult.builder()
                                .messages(new ValidationMessage(ERROR_ACCESSING, configuration.getRemoteUri().orElse(null)))
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
    public GitRepositoryApi pull() throws RepositoryException {
        try {
            final Git git = openGit();

            handleRebase(git);

            final PullResult result = git.pull()
                    .setRemote("origin")
                    .setCredentialsProvider(configuration.toCredentialsProvider().orElse(null))
                    .setRebase(BranchConfig.BranchRebaseMode.REBASE)
                    .call();

            if (!result.getRebaseResult().getStatus().isSuccessful()) {
                handleRebase(git);
            } else if (!result.isSuccessful()) {
                throw RepositoryException.onUpdate(null);
            }

            return this;
        } catch (Exception e) {
            throw RepositoryException.onUpdate(e);
        }
    }

    @Override
    public GitRepositoryApi fetch() throws RepositoryException {
        try {
            openGit().fetch()
                    .setCredentialsProvider(configuration.toCredentialsProvider().orElse(null))
                    .setRemoveDeletedRefs(true)
                    .call();

            return this;
        } catch (Exception e) {
            throw RepositoryException.onUpdate(e);
        }
    }

    @Override
    public GitRepositoryApi revert(File file) throws RepositoryException {
        try {
            openGit().checkout().addPath(file.toString()).call();

            return this;
        } catch (Exception e) {
            throw RepositoryException.onRevert(e);
        }
    }

    @Override
    public GitRepositoryApi commitAll(String message) throws RepositoryException {
        try {
            openGit().add().addFilepattern(".").call();

            openGit().commit()
                    .setAuthor(
                            (configuration.getDisplayName().isPresent() && configuration.getEmail().isPresent())
                                    ? new PersonIdent(configuration.getDisplayName().get(), configuration.getEmail().get())
                                    : null
                    )
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
            openGit().push().setCredentialsProvider(configuration.toCredentialsProvider().orElse(null)).call();

            return this;
        } catch (Exception e) {
            throw RepositoryException.onPush(e);
        }
    }

    @Override
    public GitRepositoryApi resetHardHead() throws RepositoryException {
        try {
            removeLockIfPresent();

            openGit().reset().setMode(ResetCommand.ResetType.HARD).setRef("HEAD").call();

            return this;
        } catch (Exception e) {
            throw RepositoryException.onRevert(e);
        }
    }

    @Override
    public GitRepositoryApi merge(String branch) throws RepositoryException {
        try {
            final Git git = openGit();

            resetHardHead();

            final MergeResult result = git
                    .merge()
                    .setFastForward(MergeCommand.FastForwardMode.FF)
                    .include(git.getRepository().resolve(branch))
                    .call();

            if (!result.getMergeStatus().isSuccessful()) {
                throw RepositoryException.onBranchMerging(branch, null);
            }

            return this;
        } catch (RepositoryException e) {
            throw e;
        } catch (Exception e) {
            throw RepositoryException.onBranchMerging(branch, e);
        }
    }

    @Override
    protected void doInit() throws GitAPIException {
        Git.cloneRepository()
                .setCredentialsProvider(configuration.toCredentialsProvider().orElse(null))
                .setURI(Objects.toString(configuration.getRemoteUri().orElse(null), null))
                .setDirectory(configuration.getRepositoryLocation())
                .setBranchesToClone(singletonList(configuration.getDefaultBranch()))
                .setBranch(configuration.getDefaultBranch())
                .call();

        checkout(configuration.getDefaultBranch());
    }

    @Override
    protected void doCreateBranch(String branch) throws Exception {
        openGit().checkout()
                .setCreateBranch(true)
                .setName(branch)
                .call();
    }

    @Override
    protected void doRemoveBranch(String branch) throws Exception {
        openGit().branchDelete().setBranchNames(branch).setForce(true).call();
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
     * Opens {@link Git}, or initialize it if needed.
     */
    private Git openGit() throws IOException {
        if (git == null) {
            git = Git.open(configuration.getRepositoryLocation());
        }

        return git;
    }

    /**
     * Removes the lock on the Git index file if it exists.
     */
    private void removeLockIfPresent() throws InterruptedException {
        final File lockFile = new File(configuration.getRepositoryLocation(), ".git/index.lock");

        for (int i = 1; i <= NUMBER_ATTEMPT_REMOVE_LOCK; i++) {
            Thread.sleep(INTERVAL_BETWEEN_ATTEMPTS_REMOVE_LOCK_IN_MS);

            if (!lockFile.exists()) {
                return;
            }
        }

        FileUtils.deleteQuietly(lockFile);

        logger.warn(String.format("The lock file [%s] has been forced deleted.", lockFile));
    }

    /**
     * Handles a rebase in progress. All files from the remote origin will be used.
     */
    private void handleRebase(Git git) throws Exception {
        if (!git.getRepository().getRepositoryState().isRebasing()) {
            return;
        }

        if (!git.status().call().getConflicting().isEmpty()) {
            git.checkout()
                    .setStage(CheckoutCommand.Stage.THEIRS)
                    .setForce(true)
                    .addPath(".")
                    .call();

            git.add().addFilepattern(".").call();
        }

        final RebaseResult rebaseResult = git.rebase().setOperation(RebaseCommand.Operation.CONTINUE).call();

        if (rebaseResult.getStatus() == RebaseResult.Status.STOPPED) {
            handleRebase(git);
        }
    }
}
