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
import be.sgerard.i18n.service.repository.validation.RepositoryValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.proxy.Proxy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.InvocationTargetException;

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
    private final RepositoryValidator<RepositoryEntity> validator;

    public RepositoryManagerImpl(RepositoryEntityRepository repository,
                                 RepositoryHandler<RepositoryEntity, RepositoryCreationDto, RepositoryPatchDto> handler,
                                 LockService lockService,
                                 RepositoryListener<RepositoryEntity> listener, 
                                 RepositoryValidator<RepositoryEntity> validator) {
        this.repository = repository;
        this.handler = handler;
        this.lockService = lockService;
        this.listener = listener;
        this.validator = validator;
    }

    @Override
    public Mono<RepositoryEntity> findById(String id) throws ResourceNotFoundException {
        return repository.findById(id);
    }

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
                        validator
                                .beforePersist(rep)
                                .map(validationResult -> {
                                    ValidationException.throwIfFailed(validationResult);

                                    return rep;
                                })
                                .flatMap(repository::save)
                                .flatMap(repo ->
                                        listener.afterCreate(repo)
                                                .thenReturn(repo)
                                )
                );
    }

    @Transactional
    @Override
    public Mono<RepositoryEntity> initialize(String id) throws ResourceNotFoundException, IllegalStateException {
        return lockService.executeAndGetMono(
                id,
                () ->
                        findByIdOrDie(id)
                                .flatMap(repository ->
                                        Mono.just(repository)
                                                // TODO initializing
                                                .filter(repo -> repo.getStatus() != RepositoryStatus.INITIALIZED)
                                                .flatMap(handler::initializeRepository)
                                                .doOnNext(repo -> repo.setStatus(RepositoryStatus.INITIALIZED))
                                                .flatMap(this.repository::save)
                                                .flatMap(rep ->
                                                        listener
                                                                .afterInitialize(rep)
                                                                .thenReturn(rep)
                                                )
                                                .switchIfEmpty(Mono.just(repository))
                                                .onErrorResume(error -> {
                                                    logger.error("Error while initializing repository.", error);

                                                    repository.setStatus(RepositoryStatus.INITIALIZATION_ERROR);

                                                    return this.repository
                                                            .save(repository)
                                                            .flatMap(rep ->
                                                                    listener
                                                                            .onInitializationError(rep, error)
                                                                            .thenReturn(rep)
                                                            );
                                                })
                                ));
    }

    @Transactional
    @Override
    public Mono<RepositoryEntity> update(RepositoryPatchDto patch) throws ResourceNotFoundException, RepositoryException {
        return lockService.executeAndGetMono(
                patch.getId(),
                () ->
                        findByIdOrDie(patch.getId())
                                .flatMap(repo ->
                                        validator
                                                .beforeUpdate(repo, patch)
                                                .map(validationResult -> {
                                                    ValidationException.throwIfFailed(validationResult);

                                                    return repo;
                                                })
                                )
                                .flatMap(repo -> handler.updateRepository(repo, patch))
                                .flatMap(repository::save)
                                .flatMap(repo ->
                                        listener
                                                .afterUpdate(patch, repo)
                                                .thenReturn(repo)
                                ));
    }

    @Transactional
    @Override
    public Mono<RepositoryEntity> delete(String id) {
        return lockService.executeAndGetMono(
                id,
                () ->
                        findById(id)
                                .flatMap(repo ->
                                        validator
                                                .beforeDelete(repo)
                                                .map(validationResult -> {
                                                    ValidationException.throwIfFailed(validationResult);

                                                    return repo;
                                                })
                                                .flatMap(handler::deleteRepository)
                                                .onErrorResume(error -> {
                                                    logger.error("Error while deleting repository.", error);
                                                    return Mono.just(repo);
                                                })
                                                .flatMap(rep ->
                                                        listener.beforeDelete(rep)
                                                                .thenReturn(rep)
                                                )
                                                .flatMap(rep ->
                                                        repository.delete(rep)
                                                                .thenReturn(rep)
                                                )
                                ));
    }

    @Override
    @Transactional
    public <A extends RepositoryApi, T> Mono<T> applyGetMono(String repositoryId,
                                                             Class<A> apiType,
                                                             RepositoryApi.ApiFunction<A, Mono<T>> apiConsumer) throws RepositoryException {
        try {
            return lockService.executeAndGetMono(
                    repositoryId,
                    () ->
                            findByIdOrDie(repositoryId)
                                    .flatMap(handler::createAPI)
                                    .flatMap(api ->
                                            Mono
                                                    .just(api)
                                                    .map(apiType::cast)
                                                    .map(a -> wrapIntoProxy(apiType, a))
                                                    .flatMap(apiConsumer::apply)
                                                    .doOnCancel(api::close)
                                                    .doFinally(signalType -> api.close())
                                    ));
        } catch (LockTimeoutException e) {
            throw RepositoryException.onLockTimeout(e);
        }
    }

    @Override
    @Transactional
    public <A extends RepositoryApi, T> Flux<T> applyGetFlux(String repositoryId,
                                                             Class<A> apiType,
                                                             RepositoryApi.ApiFunction<A, Flux<T>> apiConsumer) throws RepositoryException {
        try {
            return lockService.executeAndGetFlux(
                    repositoryId,
                    () ->
                            findByIdOrDie(repositoryId)
                                    .flatMap(handler::createAPI)
                                    .flatMapMany(api ->
                                            Mono
                                                    .just(api)
                                                    .map(apiType::cast)
                                                    .map(a -> wrapIntoProxy(apiType, a))
                                                    .flatMapMany(apiConsumer::apply)
                                                    .doAfterTerminate(api::close)
                                    ));
        } catch (LockTimeoutException e) {
            throw RepositoryException.onLockTimeout(e);
        }
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
