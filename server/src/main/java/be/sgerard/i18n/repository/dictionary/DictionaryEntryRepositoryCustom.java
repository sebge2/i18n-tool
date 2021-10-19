package be.sgerard.i18n.repository.dictionary;

import be.sgerard.i18n.model.dictionary.DictionaryEntrySearchRequest;
import be.sgerard.i18n.model.dictionary.persistence.DictionaryEntryEntity;
import reactor.core.publisher.Flux;

/**
 * Repository of {@link DictionaryEntryEntity dictionary entry entities}.
 *
 * @author Sebastien Gerard
 */
public interface DictionaryEntryRepositoryCustom {

    /**
     * @see DictionaryEntryEntity#getTranslations() ()
     */
    String FIELD_TRANSLATIONS = "translations";

    /**
     * Returns the field name to target the translation in the specified locale.
     */
    static String fieldTranslation(String localeId) {
        return FIELD_TRANSLATIONS + "." + localeId;
    }

    /**
     * Finds {@link DictionaryEntryEntity dictionary entries} satisfying the specified {@link DictionaryEntrySearchRequest request}.
     */
    Flux<DictionaryEntryEntity> find(DictionaryEntrySearchRequest request);
}
