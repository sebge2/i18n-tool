package be.sgerard.poc.githuboauth.service.git;

import be.sgerard.poc.githuboauth.configuration.AppProperties;
import be.sgerard.poc.githuboauth.service.LockService;
import be.sgerard.poc.githuboauth.service.LockTimeoutException;
import be.sgerard.poc.githuboauth.service.auth.AuthenticationManager;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

/**
 * @author Sebastien Gerard
 */
@Service
public class RepositoryManagerImpl implements RepositoryManager {

    public static final String DEFAULT_BRANCH = "master";

    public static final String REFS_ORIGIN_PREFIX = "refs/remotes/origin/";

    private final String repoUri;
    private final File localRepositoryLocation;
    private final AuthenticationManager authenticationManager;
    private final LockService lockService;

    private Git git;

    public RepositoryManagerImpl(AppProperties appProperties,
                                 AuthenticationManager authenticationManager,
                                 LockService lockService) {
        this.repoUri = appProperties.getRepoCheckoutUri();
        this.localRepositoryLocation = new File(appProperties.getLocalRepositoryLocation());

        this.authenticationManager = authenticationManager;
        this.lockService = lockService;
    }

    @Override
    public void initLocalRepository() throws RepositoryException {
        try {
            if (checkRepoInitialized()) {
                throw new IllegalArgumentException("The repository [" + localRepositoryLocation + "] is already initialized.");
            }

            this.git = Git.cloneRepository()
                    .setCredentialsProvider(createProvider())
                    .setURI(repoUri)
                    .setDirectory(localRepositoryLocation)
                    .setBranchesToClone(singletonList(DEFAULT_BRANCH))
                    .setBranch(DEFAULT_BRANCH)
                    .call();
        } catch (GitAPIException e) {
            throw new RepositoryException("Error while initializing local repository.", e);
        }
    }

    @Override
    public List<String> listBranches() throws RepositoryException, LockTimeoutException {
        try {
            return lockService.executeInLock(() -> {
                refresh();

                return getGit().branchList().setListMode(ListBranchCommand.ListMode.REMOTE).call().stream()
                        .map(Ref::getName)
                        .map(name -> name.startsWith(REFS_ORIGIN_PREFIX) ? name.substring(REFS_ORIGIN_PREFIX.length()) : name)
                        .sorted((first, second) -> {
                            if (DEFAULT_BRANCH.equals(first)) {
                                return -1;
                            } else if (DEFAULT_BRANCH.equals(second)) {
                                return 1;
                            } else {
                                return first.compareTo(second);
                            }
                        })
                        .distinct()
                        .collect(toList());
            });
        } catch (LockTimeoutException | RepositoryException e) {
            throw e;
        } catch (Exception e) {
            throw new RepositoryException("Error while listing branches.", e);
        }
    }

    @Override
    public <T> T checkoutBranchAndDo(String branchName, RepositoryBrowser<T> repoConsumer) throws RepositoryException, LockTimeoutException {
        try {
            return lockService.executeInLock(() -> {
                checkoutBranch(branchName);

                refresh();

                return repoConsumer.browse(localRepositoryLocation);
            });
        } catch (LockTimeoutException | RepositoryException e) {
            throw e;
        } catch (Exception e) {
            throw new RepositoryException("Error while listing branches.", e);
        }
    }

    private void refresh() throws Exception {
        getGit().pull().setCredentialsProvider(createProvider()).call();
    }

    private void checkoutBranch(String branchName) throws Exception {
        getGit().checkout().setName(branchName).call();
    }

    private UsernamePasswordCredentialsProvider createProvider() {
        return new UsernamePasswordCredentialsProvider(authenticationManager.getCurrentAuth().getToken(), "");
    }

    private boolean checkRepoInitialized() {
        return localRepositoryLocation.exists() && FileUtils.sizeOfDirectory(localRepositoryLocation) > 0;
    }

    private Git getGit() throws Exception {
        if (git == null) {
            if(!checkRepoInitialized()){
                throw new IllegalStateException("The local repository has not been initialized. Hint: call initialize.");
            }

            git = Git.open(localRepositoryLocation);
        }

        return git;
    }

    //    private void createBranch(String branchName) throws Exception {
//        getGit().checkout()
//                .setCreateBranch(true)
//                .setName(branchName)
//                .call();
//    }
//

//
//    private void commitAll(CommitRequest request) throws Exception {
//        getGit().add().addFilepattern("*").call();
//
//        getGit().commit()
//                .setAuthor(request.getAuthorName(), request.getAuthorEmail())
//                .setMessage(request.getMessage()).call();
//    }
//
//    private void push() throws Exception {
//        getGit().push().call();
//    }
}
