package be.sgerard.i18n.service.git;

import be.sgerard.i18n.configuration.AppProperties;
import be.sgerard.i18n.model.repository.RepositoryDescriptionDto;
import be.sgerard.i18n.model.repository.RepositoryStatus;
import be.sgerard.i18n.service.LockService;
import be.sgerard.i18n.service.LockTimeoutException;
import be.sgerard.i18n.service.event.EventService;
import be.sgerard.i18n.service.security.auth.AuthenticationManager;
import org.apache.commons.io.IOUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.cglib.proxy.Proxy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static be.sgerard.i18n.model.event.Events.EVENT_UPDATED_REPOSITORY;
import static java.util.Collections.singletonList;

/**
 * @author Sebastien Gerard
 */
@Service
public class RepositoryManagerImpl implements RepositoryManager {

    public static final String DEFAULT_BRANCH = "master";

    private final String repoUri;
    private final File repositoryLocation;
    private final File repositoryLockFile;
    private final AuthenticationManager authenticationManager;
    private final LockService lockService;
    private final PullRequestManager pullRequestManager;
    private final EventService eventService;
    private final TransactionTemplate transactionTemplate;

    private Git git;

    public RepositoryManagerImpl(AppProperties appProperties,
                                 AuthenticationManager authenticationManager,
                                 LockService lockService,
                                 PullRequestManager pullRequestManager,
                                 EventService eventService, PlatformTransactionManager transactionManager) {
        this.repoUri = appProperties.getRepoCheckoutUri();

        this.repositoryLocation = appProperties.getRepositoryLocationAsFile();
        this.repositoryLockFile = new File(appProperties.getBaseDirectoryAsFile(), "repository.lock");

        this.authenticationManager = authenticationManager;
        this.lockService = lockService;
        this.pullRequestManager = pullRequestManager;
        this.eventService = eventService;

        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    @Override
    public RepositoryDescriptionDto getDescription() {
        return new RepositoryDescriptionDto(
                repoUri,
                getStatus()
        );
    }

    @Override
    public boolean initLocalRepository() throws LockTimeoutException, RepositoryException {
        try {
            if (getStatus() != RepositoryStatus.NOT_INITIALIZED) {
                return false;
            }

            return lockService.executeInLock(() -> {
                if (getStatus() != RepositoryStatus.NOT_INITIALIZED) {
                    return false;
                }

                try {
                    updateLockFile(RepositoryStatus.INITIALIZING);

                    this.eventService.broadcastEvent(EVENT_UPDATED_REPOSITORY, getDescription());

                    this.git = Git.cloneRepository()
                            .setCredentialsProvider(createProvider())
                            .setURI(repoUri)
                            .setDirectory(repositoryLocation)
                            .setBranchesToClone(singletonList(DEFAULT_BRANCH))
                            .setBranch(DEFAULT_BRANCH)
                            .call();

                    updateLockFile(RepositoryStatus.INITIALIZED);

                    this.eventService.broadcastEvent(EVENT_UPDATED_REPOSITORY, getDescription());
                } catch (Exception e) {
                    updateLockFile(RepositoryStatus.NOT_INITIALIZED);

                    throw e;
                }

                return true;
            });
        } catch (LockTimeoutException | RepositoryException e) {
            throw e;
        } catch (Exception e) {
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
    public void openInNewTx(ApiConsumer apiConsumer) throws RepositoryException, LockTimeoutException {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                open(apiConsumer);
            }
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

    @Override
    public <T> T openInNewTx(ApiTransformer<T> apiConsumer) throws RepositoryException, LockTimeoutException {
        return transactionTemplate.execute(transactionStatus -> open(apiConsumer));
    }

    private RepositoryStatus getStatus() throws RepositoryException {
        try {
            if (repositoryLockFile.exists()) {
                return RepositoryStatus.valueOf(IOUtils.toString(repositoryLockFile.toURI()));
            } else {
                return RepositoryStatus.NOT_INITIALIZED;
            }
        } catch (IOException e) {
            throw new RepositoryException("Cannot retrieve repository status.", e);
        }
    }

    private void updateLockFile(RepositoryStatus status) throws RepositoryException {
        try {
            try (FileOutputStream output = new FileOutputStream(repositoryLockFile)) {
                IOUtils.write(status.name(), output);
            }
        } catch (IOException e) {
            throw new RepositoryException("Cannot create lock file for status " + status + ".", e);
        }
    }

    private UsernamePasswordCredentialsProvider createProvider() {
        return new UsernamePasswordCredentialsProvider(authenticationManager.getCurrentAuthenticatedUserOrFail().getGitHubTokenOrFail(), "");
    }

    private Git getGit() throws Exception {
        if (git == null) {
            if (getStatus() != RepositoryStatus.INITIALIZED) {
                throw new IllegalStateException("The local repository has not been initialized. Hint: call initialize.");
            }

            git = Git.open(repositoryLocation);
        }

        return git;
    }

    private RepositoryAPI initializeAPI() throws Exception {
        final DefaultRepositoryAPI delegate = new DefaultRepositoryAPI(
                getGit(),
                repositoryLocation,
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
