package be.sgerard.i18n.service.repository;

import be.sgerard.i18n.model.repository.RepositoryStatus;
import be.sgerard.i18n.model.repository.dto.RepositoryCreationDto;
import be.sgerard.i18n.model.repository.dto.RepositoryPatchDto;
import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.repository.repository.RepositoryEntityRepository;
import be.sgerard.i18n.service.LockService;
import be.sgerard.i18n.service.LockTimeoutException;
import be.sgerard.i18n.service.ResourceNotFoundException;
import be.sgerard.i18n.service.ValidationException;
import be.sgerard.i18n.service.repository.listener.RepositoryListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.proxy.Proxy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.InvocationTargetException;

import static java.util.Arrays.asList;

/**
 * Implementation of the {@link RepositoryManager repository manager}.
 *
 * @author Sebastien Gerard
 */
@Service
public class RepositoryManagerImpl implements RepositoryManager {

    private static final Logger logger = LoggerFactory.getLogger(RepositoryManagerImpl.class);

    private final RepositoryEntityRepository repository;
    private final RepositoryHandler<RepositoryEntity, RepositoryCreationDto, RepositoryPatchDto> handler;
    private final LockService lockService;
    private final RepositoryListener<RepositoryEntity> listener;

    public RepositoryManagerImpl(RepositoryEntityRepository repository,
                                 RepositoryHandler<RepositoryEntity, RepositoryCreationDto, RepositoryPatchDto> handler,
                                 LockService lockService,
                                 RepositoryListener<RepositoryEntity> listener) {
        this.repository = repository;
        this.handler = handler;
        this.lockService = lockService;
        this.listener = listener;
    }

    @Transactional(readOnly = true)
    @Override
    public Mono<RepositoryEntity> findById(String id) throws ResourceNotFoundException {
        return repository.findById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public Flux<RepositoryEntity> findAll() {
        return repository.findAll();
    }

    @Transactional
    @Override
    public Mono<RepositoryEntity> create(RepositoryCreationDto creationDto) {
        return handler
                .createRepository(creationDto)
                .flatMap(rep ->
                        listener
                                .beforePersist(rep)
                                .map(validationResult -> {
                                    ValidationException.throwIfFailed(validationResult);

                                    return rep;
                                })
                                .flatMap(repository::save)
                                .flatMap(repo ->
                                        listener.onCreate(repo)
                                                .thenReturn(repo)
                                )
                );
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
                                        .doOnNext(repo -> repo.setStatus(RepositoryStatus.INITIALIZED))
                                        .flatMap(this::updateAndNotifyInTx)
                                        .onErrorResume(error -> {
                                            logger.error("Error while initializing repository.", error);

                                            entity.setStatus(RepositoryStatus.INITIALIZATION_ERROR);

                                            return updateAndNotifyInTx(entity);
                                        })
                                        .switchIfEmpty(Mono.just(entity))
                        )
        );
    }

    @Transactional
    @Override
    public Mono<RepositoryEntity> update(RepositoryPatchDto patchDto) throws ResourceNotFoundException, RepositoryException {
        return lockService.executeInLock(() ->
                findByIdOrDie(patchDto.getId())
                        .flatMap(repo ->
                                listener
                                        .beforeUpdate(repo, patchDto)
                                        .map(validationResult -> {
                                            ValidationException.throwIfFailed(validationResult);

                                            return repo;
                                        })
                                        .flatMap(rep -> handler.updateRepository(rep, patchDto))
                                        .flatMap(this::updateAndNotifyInTx)
                        )
        );
    }

    @Transactional
    @Override
    public Mono<RepositoryEntity> delete(String id) {
        return findById(id)
                .flatMap(repo ->
                        listener
                                .beforeDelete(repo)
                                .map(validationResult -> {
                                    ValidationException.throwIfFailed(validationResult);

                                    return repo;
                                })
                                .flatMap(handler::deleteRepository)
                                .flatMap(rep ->
                                        repository.delete(rep)
                                                .thenReturn(rep)
                                )
                                .flatMap(rep ->
                                        listener
                                                .onDelete(rep)
                                                .thenReturn(rep)
                                )
                );
    }

    @Override
    @Transactional
    public <A extends RepositoryApi, T> Mono<T> applyOnRepository(String repositoryId,
                                                                  Class<A> apiType,
                                                                  RepositoryApi.ApiFunction<A, T> apiConsumer) throws RepositoryException {
        try {
            return lockService.executeInLock(() ->
                    findByIdOrDie(repositoryId)
                            .flatMap(handler::createAPI)
                            .map(apiType::cast)
                            .map(api -> wrapIntoProxy(apiType, api))
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
        return listener
                .onUpdate(updated)
                .then(Mono.defer(() -> repository.save(updated))); // TODO separate tx
    }

    /**
     * Wraps the specified api into a proxy insuring that every call is performed until the api is
     * {@link RepositoryApi#isClosed() closed}.
     */
    @SuppressWarnings("unchecked")
    private <A extends RepositoryApi> A wrapIntoProxy(Class<A> apiType, A api) {
        return (A) Proxy.newProxyInstance(
                apiType.getClassLoader(),
                new Class<?>[]{apiType},
                (o, method, objects) -> {
                    if (!"close".equals(method.getName()) && api.isClosed()) {
                        throw new IllegalStateException("Cannot access the API once closed.");
                    }

                    try {
                        final Object result = method.invoke(api, objects);

                        if (result == api) {
                            return o;
                        }

                        return result;
                    } catch (InvocationTargetException e) {
                        throw e.getCause();
                    }
                }
        );
    }
}
