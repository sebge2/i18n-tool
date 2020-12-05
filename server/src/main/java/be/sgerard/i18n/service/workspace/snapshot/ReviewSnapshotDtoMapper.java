package be.sgerard.i18n.service.workspace.snapshot;

import be.sgerard.i18n.model.workspace.persistence.AbstractReviewEntity;
import be.sgerard.i18n.model.workspace.snapshot.AbstractReviewSnapshotDto;
import reactor.core.publisher.Mono;

/**
 * Mapper of DTO to {@link AbstractReviewEntity review} entities and the other way around.
 *
 * @author Sebastien Gerard
 */
public interface ReviewSnapshotDtoMapper<E extends AbstractReviewEntity, D extends AbstractReviewSnapshotDto> {

    /**
     * Returns whether the specified DTO is supported.
     */
    boolean support(AbstractReviewSnapshotDto dto);

    /**
     * Returns whether the specified entity is supported.
     */
    boolean support(AbstractReviewEntity review);

    /**
     * Maps the entity from its DTO representation.
     */
    Mono<E> mapFromDto(D dto);

    /**
     * Maps the entity to its DTO representation.
     */
    Mono<D> mapToDto(E entity);
}
