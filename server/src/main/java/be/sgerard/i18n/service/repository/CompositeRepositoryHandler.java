package be.sgerard.i18n.service.repository;

import be.sgerard.i18n.model.repository.RepositoryType;
import be.sgerard.i18n.model.repository.dto.RepositoryCreationDto;
import be.sgerard.i18n.model.repository.dto.RepositoryPatchDto;
import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.model.security.repository.RepositoryCredentials;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Composite {@link RepositoryHandler repository handler}.
 *
 * @author Sebastien Gerard
 */
@Component
@Primary
public class CompositeRepositoryHandler implements RepositoryHandler<RepositoryEntity, RepositoryCreationDto, RepositoryPatchDto, RepositoryCredentials> {

    private final List<RepositoryHandler<RepositoryEntity, RepositoryCreationDto, RepositoryPatchDto, RepositoryCredentials>> handlers;

    @Lazy
    @SuppressWarnings("unchecked")
    public CompositeRepositoryHandler(List<RepositoryHandler<?, ?, ?, ?>> handlers) {
        this.handlers = (List<RepositoryHandler<RepositoryEntity, RepositoryCreationDto, RepositoryPatchDto, RepositoryCredentials>>) (List<?>) handlers;
    }

    @Override
    public boolean support(RepositoryType type) {
        return handlers.stream().anyMatch(handler -> handler.support(type));
    }

    @Override
    public Mono<RepositoryEntity> createRepository(RepositoryCreationDto creationDto) {
        return handlers.stream()
                .filter(handler -> handler.support(creationDto.getType()))
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("Unsupported repository [" + creationDto.getType() + "]."))
                .createRepository(creationDto);
    }

    @Override
    public Mono<RepositoryEntity> initializeRepository(RepositoryEntity repository, RepositoryCredentials credentials) {
        return handlers.stream()
                .filter(handler -> handler.support(repository.getType()))
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("Unsupported repository [" + repository.getType() + "]."))
                .initializeRepository(repository, credentials);
    }

    @Override
    public Mono<RepositoryEntity> updateRepository(RepositoryEntity repository,
                                                   RepositoryPatchDto patchDto,
                                                   RepositoryCredentials credentials) throws RepositoryException {
        return handlers.stream()
                .filter(handler -> handler.support(repository.getType()))
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("Unsupported repository [" + repository.getType() + "]."))
                .updateRepository(repository, patchDto, null);
    }

    @Override
    public Mono<RepositoryEntity> deleteRepository(RepositoryEntity repository, RepositoryCredentials credentials) throws RepositoryException {
        return handlers.stream()
                .filter(handler -> handler.support(repository.getType()))
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("Unsupported repository [" + repository.getType() + "]."))
                .deleteRepository(repository, credentials);
    }

    @Override
    public Mono<RepositoryApi> initApi(RepositoryEntity repository, RepositoryCredentials credentials) throws RepositoryException {
        return handlers.stream()
                .filter(handler -> handler.support(repository.getType()))
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("Unsupported repository [" + repository.getType() + "]."))
                .initApi(repository, credentials);
    }
}
