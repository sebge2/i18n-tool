package be.sgerard.poc.githuboauth.service.git;

import be.sgerard.poc.githuboauth.configuration.AppProperties;
import be.sgerard.poc.githuboauth.model.repository.RepositoryDescriptionDto;
import be.sgerard.poc.githuboauth.model.repository.RepositoryStatus;
import be.sgerard.poc.githuboauth.service.LockService;
import be.sgerard.poc.githuboauth.service.LockTimeoutException;
import be.sgerard.poc.githuboauth.service.event.EventService;
import be.sgerard.poc.githuboauth.service.i18n.file.TranslationFileUtils;
import be.sgerard.poc.githuboauth.service.security.auth.AuthenticationManager;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.cglib.proxy.Proxy;
import org.springframework.stereotype.Service;

import java.io.File;

import static be.sgerard.poc.githuboauth.model.event.Events.EVENT_UPDATED_REPOSITORY;
import static java.util.Collections.singletonList;

/**
 * @author Sebastien Gerard
 */
@Service
public class RepositoryManagerImpl implements RepositoryManager {

    public static final String DEFAULT_BRANCH = "master";

    private final String repoUri;
    private final File localRepositoryLocation;
    private final AuthenticationManager authenticationManager;
    private final LockService lockService;
    private final PullRequestManager pullRequestManager;
    private final EventService eventService;

    private Git git;

    public RepositoryManagerImpl(AppProperties appProperties,
                                 AuthenticationManager authenticationManager,
                                 LockService lockService,
                                 PullRequestManager pullRequestManager,
                                 EventService eventService) {
        this.repoUri = appProperties.getRepoCheckoutUri();
        this.localRepositoryLocation = new File(appProperties.getLocalRepositoryLocation());

        this.authenticationManager = authenticationManager;
        this.lockService = lockService;
        this.pullRequestManager = pullRequestManager;
        this.eventService = eventService;
    }

    @Override
    public RepositoryDescriptionDto getDescription() {
        return new RepositoryDescriptionDto(
            repoUri,
            getStatus()
        );
    }

    @Override
    public void initLocalRepository() throws RepositoryException {
        try {
            if (getStatus() != RepositoryStatus.INITIALIZED) {
                throw new IllegalArgumentException("The repository [" + localRepositoryLocation + "] is already initialized.");
            }

            this.git = Git.cloneRepository()
                .setCredentialsProvider(createProvider())
                .setURI(repoUri)
                .setDirectory(localRepositoryLocation)
                .setBranchesToClone(singletonList(DEFAULT_BRANCH))
                .setBranch(DEFAULT_BRANCH)
                .call();

            this.eventService.broadcastEvent(EVENT_UPDATED_REPOSITORY, getDescription());
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

    private RepositoryStatus getStatus() {
        return (localRepositoryLocation.exists() && TranslationFileUtils.listFiles(localRepositoryLocation).count() > 0)
            ? RepositoryStatus.INITIALIZED : RepositoryStatus.NOT_INITIALIZED;
    }

    private UsernamePasswordCredentialsProvider createProvider() {
        return new UsernamePasswordCredentialsProvider(authenticationManager.getAuthToken(), "");
    }

    private Git getGit() throws Exception {
        if (git == null) {
            if (getStatus() != RepositoryStatus.INITIALIZED) {
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
