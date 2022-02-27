package be.sgerard.i18n.model.dictionary.persistence;

import be.sgerard.i18n.model.locale.persistence.TranslationLocaleEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Entry in a dictionary. This entry translate a concept in different locales.
 *
 * @author Sebastien Gerard
 */
@Document("dictionary_entry")
@Getter
@Setter
@Accessors(chain = true)
public class DictionaryEntryEntity {

    /**
     * The unique id of this entry.
     */
    @Id
    private String id;

    /**
     * Map associating the locale id and the translation of the related concept.
     */
    @Singular
    private Map<String, String> translations;

    @PersistenceConstructor
    DictionaryEntryEntity() {
    }

    public DictionaryEntryEntity(Map<String, String> translations) {
        setId(UUID.randomUUID().toString());

        this.translations = translations;
    }

    public Optional<String> getTranslation(TranslationLocaleEntity locale) {
        return getTranslation(locale.getId());
    }

    public Optional<String> getTranslation(String localeId) {
        return Optional.ofNullable(getTranslations().get(localeId));
    }
}
