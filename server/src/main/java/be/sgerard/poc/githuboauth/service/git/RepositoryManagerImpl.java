package be.sgerard.poc.githuboauth.service.git;

import be.sgerard.poc.githuboauth.configuration.AppProperties;
import be.sgerard.poc.githuboauth.service.LockService;
import be.sgerard.poc.githuboauth.service.LockTimeoutException;
import be.sgerard.poc.githuboauth.service.i18n.file.TranslationFileUtils;
import be.sgerard.poc.githuboauth.service.security.auth.AuthenticationManager;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.cglib.proxy.Proxy;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.regex.Pattern;

import static java.util.Collections.singletonList;

/**
 * @author Sebastien Gerard
 */
@Service
public class RepositoryManagerImpl implements RepositoryManager {

    public static final String DEFAULT_BRANCH = "master";

    public static final Pattern BRANCHES_TO_KEEP = Pattern.compile("^master|release\\/[0-9]{4}.[0-9]{1,2}$");

    public static final String REFS_ORIGIN_PREFIX = "refs/remotes/origin/";

    private final String repoUri;
    private final File localRepositoryLocation;
    private final AuthenticationManager authenticationManager;
    private final LockService lockService;
    private final PullRequestManager pullRequestManager;

    private Git git;

    public RepositoryManagerImpl(AppProperties appProperties,
                                 AuthenticationManager authenticationManager,
                                 LockService lockService,
                                 PullRequestManager pullRequestManager) {
        this.repoUri = appProperties.getRepoCheckoutUri();
        this.localRepositoryLocation = new File(appProperties.getLocalRepositoryLocation());

        this.authenticationManager = authenticationManager;
        this.lockService = lockService;
        this.pullRequestManager = pullRequestManager;
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
    public void open(ApiConsumer apiConsumer) throws RepositoryException, LockTimeoutException {
        open((api) -> {
            apiConsumer.consume(api);
            return null;
        });
    }

    @Override
    public <T> T open(ApiTransformer<T> apiConsumer) throws RepositoryException, LockTimeoutException {
        try {
            return lockService.executeInLock(() -> {
                try (RepositoryAPI api = initializeAPI()) {
                    return apiConsumer.transform(api);
                }
            });
        } catch (LockTimeoutException | RepositoryException e) {
            throw e;
        } catch (Exception e) {
            throw new RepositoryException("Error while listing branches.", e);
        }
    }

    private UsernamePasswordCredentialsProvider createProvider() {
        return new UsernamePasswordCredentialsProvider(authenticationManager.getAuthToken(), "");
    }

    private boolean checkRepoInitialized() {
        return localRepositoryLocation.exists() && TranslationFileUtils.listFiles(localRepositoryLocation).count() > 0;
    }

    private Git getGit() throws Exception {
        if (git == null) {
            if (!checkRepoInitialized()) {
                throw new IllegalStateException("The local repository has not been initialized. Hint: call initialize.");
            }

            git = Git.open(localRepositoryLocation);
        }

        return git;
    }

    private RepositoryAPI initializeAPI() throws Exception {
        final DefaultRepositoryAPI delegate = new DefaultRepositoryAPI(
                getGit(),
                localRepositoryLocation,
                createProvider(),
                authenticationManager,
                pullRequestManager
        );

        return (RepositoryAPI) Proxy.newProxyInstance(
                RepositoryAPI.class.getClassLoader(),
                new Class<?>[]{RepositoryAPI.class},
                (o, method, objects) -> {
                    if (!"close".equals(method.getName()) && delegate.isClosed()) {
                        throw new IllegalStateException("Cannot access the API once closed.");
                    }

                    return method.invoke(delegate, objects);
                }
        );
    }

}
