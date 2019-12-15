package be.sgerard.i18n.service.i18n;

import be.sgerard.i18n.model.i18n.dto.TranslationLocaleCreationDto;
import be.sgerard.i18n.model.i18n.dto.TranslationLocaleDto;
import be.sgerard.i18n.model.i18n.persistence.TranslationLocaleEntity;
import be.sgerard.i18n.service.ResourceNotFoundException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Manager of locale used by translations.
 *
 * @author Sebastien Gerard
 */
public interface TranslationLocaleManager {

    /**
     * Returns the {@link TranslationLocaleEntity translation locale} having the specified id.
     */
    Mono<TranslationLocaleEntity> findById(String id);

    /**
     * Returns the {@link TranslationLocaleEntity translation locale} having the specified id.
     */
    default Mono<TranslationLocaleEntity> findByIdOrDie(String id) throws ResourceNotFoundException {
        return findById(id)
                .switchIfEmpty(Mono.error(ResourceNotFoundException.translationLocaleNotFoundException(id)));
    }

    /**
     * Finds all the {@link TranslationLocaleEntity translation locales}.
     */
    Flux<TranslationLocaleEntity> findAll();

    /**
     * Creates the {@link TranslationLocaleEntity translation locale} based on the specified
     * {@link TranslationLocaleCreationDto DTO}.
     */
    Mono<TranslationLocaleEntity> create(TranslationLocaleCreationDto locale);

    /**
     * Updates the specified {@link TranslationLocaleEntity translation locale}.
     */
    Mono<TranslationLocaleEntity> update(TranslationLocaleDto locale) throws ResourceNotFoundException;

    /**
     * Removes the {@link TranslationLocaleEntity translation locale} having the specified id.
     */
    Mono<TranslationLocaleEntity> delete(String localeId);
}
