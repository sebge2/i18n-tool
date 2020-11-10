package be.sgerard.i18n.service.security.repository;

import be.sgerard.i18n.model.security.auth.RepositoryCredentials;
import be.sgerard.i18n.model.security.auth.external.ExternalUserToken;
import be.sgerard.i18n.service.repository.RepositoryManager;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Implementation of the {@link RepositoryCredentialsManager credentials manager}.
 *
 * @author Sebastien Gerard
 */
@Service
public class RepositoryCredentialsManagerImpl implements RepositoryCredentialsManager {

    private final RepositoryManager repositoryManager;
    private final List<RepositoryCredentialsHandler> handlers;

    @Lazy
    public RepositoryCredentialsManagerImpl(RepositoryManager repositoryManager, List<RepositoryCredentialsHandler> handlers) {
        this.repositoryManager = repositoryManager;
        this.handlers = handlers;
    }

    @Override
    public Flux<RepositoryCredentials> loadAllCredentials() {
        return repositoryManager
                .findAll()
                .flatMap(repository ->
                        handlers.stream()
                                .filter(handler -> handler.support(repository))
                                .findFirst()
                                .orElseThrow(() -> new UnsupportedOperationException("Unsupported internal user and repository " + repository.getType() + "."))
                                .loadCredentials(repository)
                );
    }

    @Override
    public Mono<RepositoryCredentials> loadCredentials(String repositoryId) {
        return repositoryManager
                .findById(repositoryId)
                .flatMap(repository ->
                        handlers.stream()
                                .filter(handler -> handler.support(repository))
                                .findFirst()
                                .orElseThrow(() -> new UnsupportedOperationException("Unsupported internal user and repository " + repository.getType() + "."))
                                .loadCredentials(repository)
                );
    }

    @Override
    public Flux<RepositoryCredentials> loadAllCredentials(ExternalUserToken externalToken) {
        return repositoryManager
                .findAll()
                .flatMap(repository ->
                        handlers.stream()
                                .filter(handler -> handler.support(repository))
                                .findFirst()
                                .orElseThrow(() -> new UnsupportedOperationException("Unsupported external user and repository " + repository.getType() + "."))
                                .loadCredentials(externalToken, repository)
                );
    }

    @Override
    public Mono<RepositoryCredentials> loadCredentials(String repositoryId, ExternalUserToken token) {
        return repositoryManager
                .findById(repositoryId)
                .flatMap(repository ->
                        handlers.stream()
                                .filter(handler -> handler.support(repository))
                                .findFirst()
                                .orElseThrow(() -> new UnsupportedOperationException("Unsupported external user and repository " + repository.getType() + "."))
                                .loadCredentials(token, repository)
                );
    }
}
