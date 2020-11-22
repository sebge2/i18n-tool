package be.sgerard.i18n.service.user.snapshot;

import be.sgerard.i18n.model.user.persistence.UserEntity;
import be.sgerard.i18n.model.user.snapshot.UserSnapshotDto;
import reactor.core.publisher.Mono;

/**
 * Mapper of DTO to {@link UserEntity user} entities and the other way around.
 *
 * @author Sebastien Gerard
 */
public interface UserSnapshotDtoMapper<E extends UserEntity, D extends UserSnapshotDto> {

    /**
     * Returns whether the specified DTO is supported.
     */
    boolean support(UserSnapshotDto dto);

    /**
     * Returns whether the specified entity is supported.
     */
    boolean support(UserEntity user);

    /**
     * Maps the entity from its DTO representation.
     */
    Mono<E> mapFromDto(D dto);

    /**
     * Maps the entity to its DTO representation.
     */
    Mono<D> mapToDto(E entity);
}
