package be.sgerard.i18n.service.workspace;

import be.sgerard.i18n.model.i18n.WorkspaceStatus;
import be.sgerard.i18n.model.i18n.persistence.WorkspaceEntity;
import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.repository.i18n.WorkspaceRepository;
import be.sgerard.i18n.service.LockTimeoutException;
import be.sgerard.i18n.service.ResourceNotFoundException;
import be.sgerard.i18n.service.ValidationException;
import be.sgerard.i18n.service.i18n.TranslationManager;
import be.sgerard.i18n.service.repository.RepositoryException;
import be.sgerard.i18n.service.repository.RepositoryManager;
import be.sgerard.i18n.service.workspace.listener.WorkspaceListener;
import be.sgerard.i18n.service.workspace.strategy.WorkspaceTranslationsStrategy;
import be.sgerard.i18n.support.ReactiveUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Implementation of the {@link WorkspaceManager workspace manager}.
 *
 * @author Sebastien Gerard
 */
@Service
public class WorkspaceManagerImpl implements WorkspaceManager {

    private static final Logger logger = LoggerFactory.getLogger(WorkspaceManagerImpl.class);

    private final WorkspaceRepository repository;
    private final RepositoryManager repositoryManager;
    private final TranslationManager translationManager;
    private final WorkspaceListener listener;
    private final WorkspaceTranslationsStrategy translationsStrategy;

    public WorkspaceManagerImpl(WorkspaceRepository repository,
                                RepositoryManager repositoryManager,
                                TranslationManager translationManager,
                                WorkspaceListener listener,
                                WorkspaceTranslationsStrategy translationsStrategy) {
        this.repositoryManager = repositoryManager;
        this.translationManager = translationManager;
        this.repository = repository;
        this.listener = listener;
        this.translationsStrategy = translationsStrategy;
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<WorkspaceEntity> findAll() {
        return Flux.fromStream(repository.findAll().stream());
    }

    @Override
    public Flux<WorkspaceEntity> findAll(String repositoryId) {
        return Flux.fromStream(repository.findByRepositoryId(repositoryId));
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<WorkspaceEntity> findById(String id) {
        return Mono.justOrEmpty(repository.findById(id));
    }

    @Override
    @Transactional
    public Flux<WorkspaceEntity> synchronize(String repositoryId) throws RepositoryException {
        return repositoryManager
                .applyOnRepository(repositoryId, api ->
                        ReactiveUtils
                                .combine(listBranches(repositoryId), findAll(repositoryId), (branch, workspace) -> branch.compareTo(workspace.getBranch()))
                                .flatMap(pair -> {
                                    if (pair.getRight() == null) {
                                        return repositoryManager
                                                .findByIdOrDie(repositoryId)
                                                .map(repository -> createWorkspace(repository, pair.getLeft()));
                                    } else {
                                        switch (pair.getRight().getStatus()) {
                                            case IN_REVIEW:
                                                if (pair.getLeft() == null) {
                                                    return delete(pair.getRight().getId());
                                                } else {
                                                    return translationsStrategy
                                                            .isReviewFinished(pair.getRight())
                                                            .flatMap(reviewFinished ->
                                                                    reviewFinished
                                                                            ? doFinishReview(pair.getRight())
                                                                            : Mono.just(pair.getRight())
                                                            );
                                                }
                                            case INITIALIZED:
                                            case NOT_INITIALIZED:
                                                if (pair.getLeft() == null) {
                                                    return delete(pair.getRight().getId());
                                                } else {
                                                    return Mono.just(pair.getRight());
                                                }
                                            default:
                                                return Flux.error(new UnsupportedOperationException("Unsupported workspace status [" + pair.getRight().getStatus() + "]."));
                                        }
                                    }
                                })
                )
                .flatMapMany(flux -> flux);
    }

    @Override
    @Transactional
    public Mono<WorkspaceEntity> initialize(String workspaceId) throws LockTimeoutException, RepositoryException {
        return findByIdOrDie(workspaceId)
                .map(workspace -> {
                    if (workspace.getStatus() == WorkspaceStatus.INITIALIZED) {
                        return workspace;
                    }

                    ValidationException.throwIfFailed(listener.beforeInitialize(workspace));

                    logger.info("Initialing workspace {}.", workspaceId);

                    return workspace;
                })
                .flatMap(translationsStrategy::onInitialize)
                .map(workspace -> {
                    workspace.setStatus(WorkspaceStatus.INITIALIZED);

                    listener.onInitialize(workspace);

                    return workspace;
                });
    }

    @Override
    @Transactional
    public Mono<WorkspaceEntity> publish(String workspaceId, String message) throws ResourceNotFoundException, LockTimeoutException, RepositoryException {
        return findByIdOrDie(workspaceId)
                .flatMap(workspace -> {
                    if (workspace.getStatus() == WorkspaceStatus.IN_REVIEW) {
                        return Mono.just(workspace);
                    }

                    ValidationException.throwIfFailed(listener.beforePublish(workspace));

                    logger.info("Start publishing workspace {}.", workspaceId);

                    return translationsStrategy
                            .onPublish(workspace)
                            .flatMap(reviewStarted -> {
                                if (reviewStarted) {
                                    workspace.setStatus(WorkspaceStatus.IN_REVIEW);

                                    listener.onReview(workspace);

                                    return Mono.just(workspace);
                                } else {
                                    return delete(workspace.getId())
                                            .then(createWorkspaceIfNeeded(workspace));
                                }
                            });
                });
    }

    @Override
    @Transactional
    public Mono<WorkspaceEntity> finishReview(String workspaceId) throws ResourceNotFoundException, RepositoryException {
        return findById(workspaceId)
                .flatMap(this::doFinishReview);
    }

    @Override
    @Transactional
    public Mono<Void> updateTranslations(String workspaceId, Map<String, String> translations) throws ResourceNotFoundException {
        final WorkspaceEntity workspace = repository.findById(workspaceId)
                .orElseThrow(() -> ResourceNotFoundException.workspaceNotFoundException(workspaceId));

        if (workspace.getStatus() != WorkspaceStatus.INITIALIZED) {
            throw new IllegalStateException("Cannot update translations of workspace [" + workspaceId + "], the status "
                    + workspace.getStatus() + " does not allow it.");
        }

        translationManager.updateTranslations(workspace, translations);

        return Mono.empty();
    }

    @Override
    @Transactional
    public Mono<WorkspaceEntity> delete(String workspaceId) throws RepositoryException, LockTimeoutException {
        return Mono.justOrEmpty(repository.findById(workspaceId))
                .flatMap(translationsStrategy::onDelete)
                .map(workspace -> {
                    listener.onDelete(workspace);

                    logger.info("The workspace {} has been deleted.", workspaceId);

                    repository.delete(workspace);

                    return workspace;
                });
    }

    /**
     * Returns all the branches of the specified repository.
     */
    private Flux<String> listBranches(String repositoryId) {
        return repositoryManager.findByIdOrDie(repositoryId)
                .flatMapMany(translationsStrategy::listBranches);
    }

    /**
     * Recreates a new {@link WorkspaceEntity workspace} based on the same branch as the specified workspace.
     * This new workspace is created only if the branch still exists.
     */
    private Mono<WorkspaceEntity> createWorkspaceIfNeeded(WorkspaceEntity workspace) {
        return translationsStrategy
                .listBranches(workspace.getRepository())
                .hasElement(workspace.getBranch())
                .filter(present -> present)
                .map(present -> createWorkspace(workspace.getRepository(), workspace.getBranch()));
    }

    /**
     * Creates a new {@link WorkspaceEntity workspace} based on the specified branch.
     */
    private WorkspaceEntity createWorkspace(RepositoryEntity repository, String branch) {
        final WorkspaceEntity workspaceEntity = new WorkspaceEntity(repository, branch);

        listener.onCreate(workspaceEntity);

        logger.info("The workspace {} has been created.", workspaceEntity);

        this.repository.save(workspaceEntity);

        return workspaceEntity;
    }

    /**
     * Terminates the review on the specified workspace.
     */
    private Mono<WorkspaceEntity> doFinishReview(WorkspaceEntity workspace) throws LockTimeoutException, RepositoryException {
        ValidationException.throwIfFailed(listener.beforeFinishReview(workspace));

        logger.info("The review is now finished, deleting the workspace {} and then creates a new one.", workspace.getId());

        return delete(workspace.getId())
                .then(createWorkspaceIfNeeded(workspace));
    }
}
