package be.sgerard.i18n.service.i18n;

import be.sgerard.i18n.model.i18n.dto.TranslationLocaleCreationDto;
import be.sgerard.i18n.model.i18n.dto.TranslationLocaleDto;
import be.sgerard.i18n.model.i18n.persistence.TranslationLocaleEntity;
import be.sgerard.i18n.service.ResourceNotFoundException;

import java.util.Collection;

/**
 * Manager of locale used by translations.
 *
 * @author Sebastien Gerard
 */
public interface TranslationLocaleManager {

    /**
     * Returns the {@link TranslationLocaleEntity translation locale} having the specified id.
     */
    TranslationLocaleEntity findById(String id) throws ResourceNotFoundException;

    /**
     * Finds all the {@link TranslationLocaleEntity translation locales}.
     */
    Collection<TranslationLocaleEntity> findAll();

    /**
     * Creates the {@link TranslationLocaleEntity translation locale} based on the specified
     * {@link TranslationLocaleCreationDto DTO}.
     */
    TranslationLocaleEntity create(TranslationLocaleCreationDto locale);

    /**
     * Updates the specified {@link TranslationLocaleEntity translation locale}.
     */
    TranslationLocaleEntity update(TranslationLocaleDto locale) throws ResourceNotFoundException;

    /**
     * Removes the {@link TranslationLocaleEntity translation locale} having the specified id.
     */
    void delete(String localeId);
}
