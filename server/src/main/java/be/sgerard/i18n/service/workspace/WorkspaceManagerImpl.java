package be.sgerard.i18n.service.workspace;

import be.sgerard.i18n.model.repository.RepositoryStatus;
import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.model.workspace.WorkspaceStatus;
import be.sgerard.i18n.model.workspace.persistence.WorkspaceEntity;
import be.sgerard.i18n.repository.workspace.WorkspaceRepository;
import be.sgerard.i18n.service.ResourceNotFoundException;
import be.sgerard.i18n.service.ValidationException;
import be.sgerard.i18n.service.repository.RepositoryException;
import be.sgerard.i18n.service.repository.RepositoryManager;
import be.sgerard.i18n.service.workspace.listener.WorkspaceListener;
import be.sgerard.i18n.service.workspace.strategy.WorkspaceTranslationsStrategy;
import be.sgerard.i18n.service.workspace.validator.WorkspaceValidator;
import be.sgerard.i18n.support.ReactiveUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;

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
    private final WorkspaceValidator validator;
    private final WorkspaceTranslationsStrategy translationsStrategy;

    public WorkspaceManagerImpl(WorkspaceRepository repository,
                                RepositoryManager repositoryManager,
                                WorkspaceListener listener,
                                WorkspaceValidator validator,
                                WorkspaceTranslationsStrategy translationsStrategy) {
        this.repository = repository;
        this.repositoryManager = repositoryManager;
        this.listener = listener;
        this.validator = validator;
        this.translationsStrategy = translationsStrategy;
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<WorkspaceEntity> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<WorkspaceEntity> findAll(String repositoryId) {
        return repository.findByRepository(repositoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<WorkspaceEntity> findById(String id) {
        return repository.findById(id);
    }

    @Override
    @Transactional
    public Flux<WorkspaceEntity> synchronize(String repositoryId) throws RepositoryException {
        return repositoryManager
                .applyGetFlux(repositoryId, api ->
                        ReactiveUtils
                                .combine(
                                        listBranches(repositoryId),
                                        findAll(repositoryId).sort(Comparator.comparing(WorkspaceEntity::getBranch)),
                                        (branch, workspace) -> branch.compareTo(workspace.getBranch())
                                )
                                .flatMap(pair -> {
                                    final String matchingBranch = pair.getLeft();
                                    final WorkspaceEntity matchingWorkspace = pair.getRight();

                                    return synchronize(matchingBranch, matchingWorkspace, repositoryId);
                                })
                );
    }

    @Override
    @Transactional
    public Mono<WorkspaceEntity> initialize(String workspaceId) throws RepositoryException {
        return findByIdOrDie(workspaceId)
                .flatMap(workspace -> {
                    if (workspace.getStatus() == WorkspaceStatus.INITIALIZED) {
                        return Mono.just(workspace);
                    }

                    return validator
                            .beforeInitialize(workspace)
                            .map(validationResult -> {
                                ValidationException.throwIfFailed(validationResult);

                                logger.info("Initializing workspace [{}] alias [{}].", workspace.getBranch(), workspaceId);

                                return workspace;
                            })
                            .flatMap(translationsStrategy::onInitialize)
                            .doOnNext(wk -> wk.setStatus(WorkspaceStatus.INITIALIZED))
                            .flatMap(this::update)
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

                    return validator
                            .beforePublish(workspace)
                            .map(validationResult -> {
                                ValidationException.throwIfFailed(validationResult);

                                logger.info("Start publishing workspace [{}] alias [{}].", workspace.getBranch(), workspaceId);

                                return workspace;
                            })
                            .flatMap(wk -> translationsStrategy.onPublish(wk, message))
                            .flatMap(reviewStarted -> {
                                if (workspace.getReview().isPresent()) {
                                    workspace.setStatus(WorkspaceStatus.IN_REVIEW);

                                    listener.onReview(workspace);

                                    return update(workspace);
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
    public Mono<WorkspaceEntity> update(WorkspaceEntity workspace) throws ResourceNotFoundException, RepositoryException {
        return validator
                .beforeUpdate(workspace)
                .then(
                        repository
                                .save(workspace)
                                .flatMap(w ->
                                        listener.onUpdate(w)
                                                .thenReturn(w)
                                )
                );
    }

    @Override
    @Transactional
    public Mono<WorkspaceEntity> delete(String workspaceId) throws RepositoryException {
        return findById(workspaceId)
                .flatMap(translationsStrategy::onDelete)
                .flatMap(workspace -> {
                    logger.info("The workspace [{}] alias [{}] has been deleted.", workspace.getBranch(), workspaceId);

                    return listener
                            .onDelete(workspace)
                            .thenReturn(workspace);
                })
                .flatMap(workspace ->
                        repository.delete(workspace)
                                .thenReturn(workspace)
                );
    }

    /**
     * Returns all the branches of the specified repository.
     */
    private Flux<String> listBranches(String repositoryId) {
        return repositoryManager.findByIdOrDie(repositoryId)
                .flatMapMany(repository -> {
                    if (repository.getStatus() != RepositoryStatus.INITIALIZED) {
                        return Flux.empty();
                    }

                    return translationsStrategy.listBranches(repository);
                })
                .sort(String::compareTo);
    }

    /**
     * Synchronizes the matching branch (can be <tt>null</tt>) and the matching workspace (can be <tt>null</tt>).
     */
    private Mono<WorkspaceEntity> synchronize(String matchingBranch, WorkspaceEntity matchingWorkspace, String repositoryId) {
        if (matchingWorkspace == null) {
            logger.info("Synchronize, there is no workspace for the branch [{}], let's create it.", matchingBranch);

            return repositoryManager
                    .findByIdOrDie(repositoryId)
                    .flatMap(repository -> createWorkspace(repository, matchingBranch));
        } else {
            switch (matchingWorkspace.getStatus()) {
                case IN_REVIEW:
                    if (matchingBranch == null) {
                        logger.info("Synchronize, there is no branch anymore for the workspace [{}] alias [{}], let's delete it.",
                                matchingWorkspace.getBranch(), matchingWorkspace.getId());

                        return delete(matchingWorkspace.getId())
                                .then(Mono.empty());
                    } else {
                        logger.info("Synchronize, let's check the review status for the workspace [{}] alias [{}].",
                                matchingWorkspace.getBranch(), matchingWorkspace.getId());

                        return repositoryManager
                                .findByIdOrDie(repositoryId)
                                .flatMap(repository -> translationsStrategy.isReviewFinished(matchingWorkspace, repository))
                                .flatMap(reviewFinished ->
                                        reviewFinished
                                                ? doFinishReview(matchingWorkspace)
                                                : Mono.just(matchingWorkspace)
                                );
                    }
                case INITIALIZED:
                case NOT_INITIALIZED:
                    if (matchingBranch == null) {
                        logger.info("Synchronize, there is no branch anymore for the workspace [{}] alias [{}], let's delete it.",
                                matchingWorkspace.getBranch(), matchingWorkspace.getId());

                        return delete(matchingWorkspace.getId())
                                .then(Mono.empty());
                    } else {
                        logger.info("Synchronize, the branch for the workspace [{}] alias [{}] is still there, don't touch.",
                                matchingWorkspace.getBranch(), matchingWorkspace.getId());

                        return Mono.just(matchingWorkspace);
                    }
                default:
                    return Mono.error(new UnsupportedOperationException("Unsupported workspace status [" + matchingWorkspace.getStatus() + "]."));
            }
        }
    }

    /**
     * Recreates a new {@link WorkspaceEntity workspace} based on the same branch as the specified workspace.
     * This new workspace is created only if the branch still exists.
     */
    private Mono<WorkspaceEntity> createWorkspaceIfNeeded(WorkspaceEntity workspace) {
        return repositoryManager
                .findByIdOrDie(workspace.getRepository())
                .flatMap(repository ->
                        translationsStrategy
                                .listBranches(repository)
                                .hasElement(workspace.getBranch())
                                .filter(present -> present)
                                .flatMap(present -> createWorkspace(repository, workspace.getBranch()))
                );
    }

    /**
     * Creates a new {@link WorkspaceEntity workspace} based on the specified branch.
     */
    private Mono<WorkspaceEntity> createWorkspace(RepositoryEntity repository, String branch) {
        return Mono
                .just(new WorkspaceEntity(repository.getId(), branch))
                .flatMap(this.repository::save)
                .doOnNext(workspace -> logger.info("The workspace [{}] alias [{}] has been created.", workspace.getBranch(), workspace.getId()))
                .flatMap(workspace -> listener.onCreate(workspace).thenReturn(workspace))
                .flatMap(workspace ->
                        translationsStrategy.initializeOnCreate(workspace, repository)
                                ? initialize(workspace.getId())
                                : Mono.just(workspace)
                );
    }

    /**
     * Terminates the review on the specified workspace.
     */
    private Mono<WorkspaceEntity> doFinishReview(WorkspaceEntity workspace) throws RepositoryException {
        return validator
                .beforeFinishReview(workspace)
                .map(validationResult -> {
                    ValidationException.throwIfFailed(validationResult);

                    logger.info("The review is now finished, deleting the workspace [{}] alias [{}] and then creates a new one.", workspace.getBranch(), workspace.getId());

                    return workspace;
                })
                .flatMap(wk -> delete(wk.getId()))
                .then(createWorkspaceIfNeeded(workspace));
    }
}
