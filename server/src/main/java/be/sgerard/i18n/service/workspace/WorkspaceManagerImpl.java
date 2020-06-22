package be.sgerard.i18n.service.workspace;

import be.sgerard.i18n.model.repository.RepositoryStatus;
import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.model.workspace.WorkspaceEntity;
import be.sgerard.i18n.model.workspace.WorkspaceStatus;
import be.sgerard.i18n.repository.workspace.WorkspaceRepository;
import be.sgerard.i18n.service.ResourceNotFoundException;
import be.sgerard.i18n.service.ValidationException;
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

import static java.util.Arrays.asList;

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
    private final WorkspaceListener listener;
    private final WorkspaceTranslationsStrategy translationsStrategy;

    public WorkspaceManagerImpl(WorkspaceRepository repository,
                                RepositoryManager repositoryManager,
                                WorkspaceListener listener,
                                WorkspaceTranslationsStrategy translationsStrategy) {
        this.repositoryManager = repositoryManager;
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
    @Transactional(readOnly = true)
    public Flux<WorkspaceEntity> findAll(String repositoryId) {
        return Flux.fromIterable(repository.findByRepositoryId(repositoryId));
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
                                                .flatMap(repository -> createWorkspace(repository, pair.getLeft()));
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
    public Mono<WorkspaceEntity> initialize(String workspaceId) throws RepositoryException {
        return findByIdOrDie(workspaceId)
                .flatMap(workspace -> {
                    if (workspace.getStatus() == WorkspaceStatus.INITIALIZED) {
                        return Mono.just(workspace);
                    }

                    return listener
                            .beforeInitialize(workspace)
                            .map(validationResult -> {
                                ValidationException.throwIfFailed(validationResult);

                                logger.info("Initialing workspace {}.", workspaceId);

                                return workspace;
                            })
                            .flatMap(translationsStrategy::onInitialize)
                            .doOnNext(wk -> wk.setStatus(WorkspaceStatus.INITIALIZED))
                            .flatMap(wk -> listener.onInitialize(wk).thenReturn(wk));
                });
    }

    @Override
    @Transactional
    public Mono<WorkspaceEntity> publish(String workspaceId, String message) throws ResourceNotFoundException, RepositoryException {
        return findByIdOrDie(workspaceId)
                .flatMap(workspace -> {
                    if (workspace.getStatus() == WorkspaceStatus.IN_REVIEW) {
                        return Mono.just(workspace);
                    }

                    return listener
                            .beforePublish(workspace)
                            .map(validationResult -> {
                                ValidationException.throwIfFailed(validationResult);

                                logger.info("Start publishing workspace {}.", workspaceId);

                                return workspace;
                            })
                            .flatMap(wk -> translationsStrategy.onPublish(wk, message))
                            .flatMap(reviewStarted -> {
                                if (workspace.getReview().isPresent()) {
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
        return findByIdOrDie(workspaceId)
                .flatMap(this::doFinishReview);
    }

    @Override
    @Transactional
    public Mono<WorkspaceEntity> delete(String workspaceId) throws RepositoryException {
        return findById(workspaceId)
                .flatMap(translationsStrategy::onDelete)
                .map(workspace -> {
                    listener.onDelete(workspace);

                    logger.info("The workspace {} has been deleted.", workspaceId);

                    workspace.getRepository().deleteWorkspace(workspace);
                    repository.delete(workspace);

                    return workspace;
                });
    }

    /**
     * Returns all the branches of the specified repository.
     */
    private Flux<String> listBranches(String repositoryId) {
        return repositoryManager.findByIdOrDie(repositoryId)
                .flatMapMany(repository -> {
                    if (!asList(RepositoryStatus.INITIALIZING, RepositoryStatus.INITIALIZED).contains(repository.getStatus())) {
                        return Flux.empty();
                    }

                    return translationsStrategy.listBranches(repository);
                });
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
                .flatMap(present -> createWorkspace(workspace.getRepository(), workspace.getBranch()));
    }

    /**
     * Creates a new {@link WorkspaceEntity workspace} based on the specified branch.
     */
    private Mono<WorkspaceEntity> createWorkspace(RepositoryEntity repository, String branch) {
        return Mono
                .just(new WorkspaceEntity(repository, branch))
                .map(this.repository::save)
                .doOnNext(workspace -> {
                    workspace.getRepository().addWorkspace(workspace); // TODO strange

                    logger.info("The workspace {} has been created.", workspace);
                })
                .flatMap(workspace -> listener.onCreate(workspace).thenReturn(workspace));
    }

    /**
     * Terminates the review on the specified workspace.
     */
    private Mono<WorkspaceEntity> doFinishReview(WorkspaceEntity workspace) throws RepositoryException {
        return listener
                .beforeFinishReview(workspace)
                .map(validationResult -> {
                    ValidationException.throwIfFailed(validationResult);

                    logger.info("The review is now finished, deleting the workspace {} and then creates a new one.", workspace.getId());

                    return workspace;
                })
                .flatMap(wk -> delete(wk.getId()))
                .then(createWorkspaceIfNeeded(workspace));
    }
}
