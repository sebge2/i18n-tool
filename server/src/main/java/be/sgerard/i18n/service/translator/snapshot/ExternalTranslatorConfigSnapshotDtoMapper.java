package be.sgerard.i18n.service.translator.snapshot;

import be.sgerard.i18n.model.translator.persistence.ExternalTranslatorConfigEntity;
import be.sgerard.i18n.model.translator.snapshot.ExternalTranslatorConfigSnapshotDto;
import reactor.core.publisher.Mono;

/**
 * Mapper of DTO to {@link ExternalTranslatorConfigEntity translator configuration} entities and the other way around.
 *
 * @author Sebastien Gerard
 */
public interface ExternalTranslatorConfigSnapshotDtoMapper<E extends ExternalTranslatorConfigEntity, D extends ExternalTranslatorConfigSnapshotDto> {

    /**
     * Returns whether the specified DTO is supported.
     */
    boolean support(ExternalTranslatorConfigSnapshotDto dto);

    /**
     * Returns whether the specified entity is supported.
     */
    boolean support(ExternalTranslatorConfigEntity translator);

    /**
     * Maps the entity from its DTO representation.
     */
    Mono<E> mapFromDto(D dto);

    /**
     * Maps the entity to its DTO representation.
     */
    Mono<D> mapToDto(E entity);
}
