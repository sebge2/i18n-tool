package be.sgerard.i18n.service.translator;

import be.sgerard.i18n.model.translator.dto.ExternalTranslatorConfigDto;
import be.sgerard.i18n.model.translator.persistence.ExternalTranslatorConfigEntity;
import be.sgerard.i18n.service.ResourceNotFoundException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Manager dealing with the {@link ExternalTranslatorConfigEntity configuration} of external translators.
 */
public interface ExternalTranslatorConfigManager {

    /**
     * Finds all {@link ExternalTranslatorConfigEntity configurations}.
     */
    Flux<ExternalTranslatorConfigEntity> findAll();

    /**
     * Returns the {@link ExternalTranslatorConfigEntity#getId() configuration} having the specified id.
     */
    Mono<ExternalTranslatorConfigEntity> findById(String id);

    /**
     * Returns the {@link ExternalTranslatorConfigEntity#getId() configuration} having the specified id.
     */
    default Mono<ExternalTranslatorConfigEntity> findByIdOrDie(String id) {
        return findById(id)
                .switchIfEmpty(Mono.error(ResourceNotFoundException.externalTranslatorConfigNotFoundException(id)));
    }

    /**
     * Creates a new {@link ExternalTranslatorConfigEntity configuration}.
     */
    Mono<ExternalTranslatorConfigEntity> create(ExternalTranslatorConfigDto config);

    /**
     * Creates a new {@link ExternalTranslatorConfigEntity configuration}.
     */
    Mono<ExternalTranslatorConfigEntity> create(ExternalTranslatorConfigEntity config);

    /**
     * Updates the specified external translator configuration.
     */
    Mono<ExternalTranslatorConfigEntity>  update(ExternalTranslatorConfigDto config);

    /**
     * Deletes the configuration having the specified {@link ExternalTranslatorConfigEntity#getId() id}.
     */
    Mono<ExternalTranslatorConfigEntity> delete(String id);
}
