package be.sgerard.i18n.service.repository;

import be.sgerard.i18n.model.repository.RepositoryStatus;
import be.sgerard.i18n.model.repository.dto.RepositoryCreationDto;
import be.sgerard.i18n.model.repository.dto.RepositoryPatchDto;
import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.model.validation.ValidationResult;
import be.sgerard.i18n.repository.repository.RepositoryEntityRepository;
import be.sgerard.i18n.service.LockService;
import be.sgerard.i18n.service.LockTimeoutException;
import be.sgerard.i18n.service.ResourceNotFoundException;
import be.sgerard.i18n.service.ValidationException;
import be.sgerard.i18n.service.repository.listener.RepositoryListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static java.util.Arrays.asList;

/**
 * Implementation of the {@link RepositoryManager repository manager}.
 *
 * @author Sebastien Gerard
 */
@Service
public class RepositoryManagerImpl implements RepositoryManager {

    private final RepositoryEntityRepository repository;
    private final RepositoryHandler<RepositoryEntity, RepositoryCreationDto, RepositoryPatchDto> handler;
    private final LockService lockService;
    private final RepositoryListener<RepositoryEntity> listener;
    private final TransactionTemplate transactionTemplate;

    public RepositoryManagerImpl(RepositoryEntityRepository repository,
                                 RepositoryHandler<RepositoryEntity, RepositoryCreationDto, RepositoryPatchDto> handler,
                                 LockService lockService,
                                 RepositoryListener<RepositoryEntity> listener,
                                 PlatformTransactionManager platformTransactionManager) {
        this.repository = repository;
        this.handler = handler;
        this.lockService = lockService;
        this.listener = listener;
        this.transactionTemplate = new TransactionTemplate(platformTransactionManager);
        this.transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    @Transactional(readOnly = true)
    @Override
    public Mono<RepositoryEntity> findById(String id) throws ResourceNotFoundException {
        return Mono.justOrEmpty(repository.findById(id));
    }

    @Transactional(readOnly = true)
    @Override
    public Flux<RepositoryEntity> findAll() {
        return Flux.fromStream(repository.findAll().stream());
    }

    @Transactional
    @Override
    public Mono<RepositoryEntity> create(RepositoryCreationDto creationDto) {
        return handler.createRepository(creationDto)
                .map(entity -> {
                    final ValidationResult validationResult = listener.beforePersist(entity);

                    ValidationException.throwIfFailed(validationResult);

                    return entity;
                })
                .map(repository::save)
                .map(entity -> {
                    listener.onCreate(entity);
                    return entity;
                });
    }

    @Transactional
    @Override
    public Mono<RepositoryEntity> initialize(String id) throws ResourceNotFoundException, IllegalStateException {
        return lockService.executeInLock(() ->
                findByIdOrDie(id)
                        .map(entity -> {
                            if (asList(RepositoryStatus.NOT_INITIALIZED, RepositoryStatus.INITIALIZATION_ERROR).contains(entity.getStatus())) {
                                entity.setStatus(RepositoryStatus.INITIALIZING);
                            }
                            return entity;
                        })
                        .flatMap(entity ->
                                updateAndNotifyInTx(entity)
                                        .filter(repo -> repo.getStatus() == RepositoryStatus.INITIALIZING)
                                        .flatMap(handler::initializeRepository)
                                        .flatMap(repo -> {
                                            repo.setStatus(RepositoryStatus.INITIALIZED);

                                            return updateAndNotifyInTx(repo);
                                        })
                                        .onErrorResume(error -> {
                                            entity.setStatus(RepositoryStatus.INITIALIZATION_ERROR);

                                            return updateAndNotifyInTx(entity);
                                        })
                        )
        );
    }

    @Transactional
    @Override
    public Mono<RepositoryEntity> update(RepositoryPatchDto patchDto) throws ResourceNotFoundException, RepositoryException {
        return lockService.executeInLock(() ->
                findByIdOrDie(patchDto.getId())
                        .map(entity -> {
                            final ValidationResult validationResult = listener.beforeUpdate(entity, patchDto);

                            ValidationException.throwIfFailed(validationResult);

                            return entity;
                        })
                        .flatMap(entity -> handler.updateRepository(entity, patchDto))
                        .flatMap(this::updateAndNotifyInTx)
        );
    }

    @Transactional
    @Override
    public Mono<Void> delete(String id) {
        return Mono
                .just(repository.findById(id))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(entity -> {
                    final ValidationResult validationResult = listener.beforeDelete(entity);

                    ValidationException.throwIfFailed(validationResult);

                    return entity;
                })
                .flatMap(handler::deleteRepository)
                .map(entity -> {
                    repository.delete(entity);
                    listener.onDelete(entity);

                    return entity;
                })
                .then();
    }

    @Override
    public <T> Mono<T> applyOnRepository(String repositoryId, RepositoryApi.ApiFunction<T> apiConsumer) throws RepositoryException {
        try {
            return lockService.executeInLock(() ->
                    findByIdOrDie(repositoryId)
                            .flatMap(handler::createAPI)
                            .map(apiConsumer::apply)
            );
        } catch (LockTimeoutException e) {
            throw RepositoryException.onLockTimeout(e);
        }
    }

    /**
     * Persists the specified entity in another transaction and sends an event.
     */
    private Mono<RepositoryEntity> updateAndNotifyInTx(RepositoryEntity updated) {
        listener.onUpdate(updated);

        return this.transactionTemplate.execute(transactionStatus -> Mono.just(repository.save(updated)));
    }
}
