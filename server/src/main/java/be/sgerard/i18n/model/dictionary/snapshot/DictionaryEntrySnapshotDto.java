package be.sgerard.i18n.model.dictionary.snapshot;

import be.sgerard.i18n.model.dictionary.persistence.DictionaryEntryEntity;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Map;

/**
 * Dto for storing a {@link DictionaryEntryEntity dictionary entries} in a snapshot.
 *
 * @author Sebastien Gerard
 */
@Getter
public class DictionaryEntrySnapshotDto {

    /**
     * @see DictionaryEntryEntity#getId()
     */
    private final String id;

    /**
     * @see DictionaryEntryEntity#getTranslations()
     */
    private final Map<String, String> translations;

    @JsonCreator
    public DictionaryEntrySnapshotDto(@JsonProperty("id") String id,
                                      @JsonProperty("translations") Map<String, String> translations) {
        this.id = id;
        this.translations = translations;
    }
}
