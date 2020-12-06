package be.sgerard.i18n.service.repository.snapshot;

import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.model.repository.snapshot.RepositorySnapshotDto;
import reactor.core.publisher.Mono;

/**
 * Mapper of DTO to {@link RepositoryEntity repository} entities and the other way around.
 *
 * @author Sebastien Gerard
 */
public interface RepositorySnapshotDtoMapper<E extends RepositoryEntity, D extends RepositorySnapshotDto> {

    /**
     * Returns whether the specified DTO is supported.
     */
    boolean support(RepositorySnapshotDto dto);

    /**
     * Returns whether the specified entity is supported.
     */
    boolean support(RepositoryEntity repository);

    /**
     * Maps the entity from its DTO representation.
     */
    Mono<E> mapFromDto(D dto);

    /**
     * Maps the entity to its DTO representation.
     */
    Mono<D> mapToDto(E entity);
}
