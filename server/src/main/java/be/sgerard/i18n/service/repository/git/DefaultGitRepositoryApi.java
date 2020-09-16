package be.sgerard.i18n.service.repository.git;

import be.sgerard.i18n.model.validation.ValidationMessage;
import be.sgerard.i18n.model.validation.ValidationResult;
import be.sgerard.i18n.service.ValidationException;
import be.sgerard.i18n.service.repository.RepositoryException;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

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
    public GitRepositoryApi pull() throws RepositoryException {
        checkNoModifiedFile();

        try {
            openGit().fetch()
                    .setCredentialsProvider(configuration.toCredentialsProvider().orElse(null))
                    .setRemoveDeletedRefs(true)
                    .call();

            final PullResult result = openGit().pull()
                    .setRemote("origin")
                    .setCredentialsProvider(configuration.toCredentialsProvider().orElse(null))
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
    public GitRepositoryApi revert(File file) throws RepositoryException {
        try {
            openGit().checkout().addPath(file.toString()).call();

            return this;
        } catch (Exception e) {
            throw RepositoryException.onRevert(e);
        } finally {
            removeModifiedFile(file);
        }
    }

    @Override
    public GitRepositoryApi commitAll(String message) throws RepositoryException {
        try {
            openGit().add().addFilepattern(".").call();

            openGit().commit()
                    .setAuthor(new PersonIdent(configuration.getDisplayName(), configuration.getEmail()))
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

            clearModifiedFiles();

            return this;
        } catch (Exception e) {
            throw RepositoryException.onPush(e);
        }
    }

    @Override
    public GitRepositoryApi resetHardHead() throws RepositoryException {
        try {
            openGit().reset().setMode(ResetCommand.ResetType.HARD).setRef("HEAD").call();

            return this;
        } catch (Exception e) {
            throw RepositoryException.onRevert(e);
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

}
